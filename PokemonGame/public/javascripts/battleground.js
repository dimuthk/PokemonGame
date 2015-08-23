var cardBackImg = "Card-Back.jpg"
var srcTag = "/assets/images/[IMG_NAME]"
var imgTag = "<img src=" + srcTag + " height=\"100%\" width=\"100%\" id=[ID] dat=\"[DAT]\"></img>" 
var energyTag = "<img src=" + srcTag + " height=\"50%\" width=\"50%\" id=[ID] dat=\"[DAT]\"></img>" 

var popUpTag = "<img src=" + srcTag + " height=\"70%\" width=\"60%\" id=\"[ID]\" dat=\"[DAT]\">" 


function establishConnection() {
	var socket = new WebSocket("ws://localhost:9000/socket")

	$("#content").data("socket", socket)

	socket.onopen = function() {
		socket.send("BUILD_MACHOP")
	}

	socket.onmessage = function (event) {
		repaintBoard(JSON.parse(event.data), socket)
	}

}

/**
 * Updates the board given the new set of JSON values returned from the server.
 */
function repaintBoard(data, socket) {
	repaintForPlayer(data.player1, true)
	repaintForPlayer(data.player2, false)
	
	$("#p1ActiveDisplay").droppable({
			accept: "*",
			drop: function(event, ui) {
          var tag = $(ui.draggable).attr("id")
          if (tag.includes("Active")) {
          	// active to active
          } else if (tag.includes("Bench")) {
          	// bench to active
            var benchIndex = tag.charAt(tag.length - 1)
            benchToActive(benchIndex)
          } else if (tag.includes("Hand")) {
          	// hand to active
          	var handIndex = tag.charAt(tag.length - 1)
		  	    handToActive(handIndex)
          }
          
		} });

  for (var i = 1; i < 6; i++) {
    $("#p1BenchDisplay" + i).droppable({
        accept: "*",
        drop : function(event, ui) {
          var tag = $(ui.draggable).attr("id")
          var benchTag = $(this).attr("id")
          var benchIndex = benchTag.charAt(benchTag.length - 1)
          if (tag.includes("Active")) {
          	// active to bench
            activeToBench(benchIndex)
          } else if (tag.includes("Bench")) {
          	// bench to bench
            var benchIndex2 = tag.charAt(tag.length - 1)
            benchToBench(benchIndex2, benchIndex)
          } else if (tag.includes("Hand")) {
          	// hand to bench
          	var handIndex = tag.charAt(tag.length - 1)
          	handToBench(handIndex, benchIndex)
          }
          
        }
    })
  }

  colorBoardIfActivated(data)
}

function colorBoardIfActivated(data) {
  if (colorBoardIfActivatedForCard(data.player1.ACTIVE)) {
    return
  }
  for (var i=0; i<data.player1.BENCH.length; i++) {
    if (colorBoardIfActivatedForCard(data.player1.BENCH[i])) {
      return
    }
  }
  if (colorBoardIfActivatedForCard(data.player2.ACTIVE)) {
    return
  }
  for (var i=0; i<data.player2.BENCH.length; i++) {
    if (colorBoardIfActivatedForCard(data.player2.BENCH[i])) {
      return
    }
  }
  // revert the board to original color if no power took place.
  $("#content").animate({backgroundColor: "gray"}, 2000)
}

function colorBoardIfActivatedForCard(card) {
  if (isPlaceholder(card)) {
    return
  }
  for (var i=0; i<card.MOVES.length; i++) {
    if (!isPlaceholder(card.MOVES[i])) {
      if (card.MOVES[i].MOVE_STATUS == "ACTIVATED") {
        $("#content").animate({backgroundColor: "green"}, 2000)
        return true
      }
    }
  }
  return false
}

function repaintForPlayer(data, p1Orient) {
	if ('PRIZES' in data) {
		var prizes = data.PRIZES
		for (var i = 0; i < prizes.length; i++) {
			var tag = (p1Orient ? "p1Prize" : "p2Prize") + (i+1)
			addImageOfItem(prizes[i], $("#" + tag), "", false)
		}
	}
	if ('DECK' in data) {
		var deck = data.DECK
		if (deck.length > 0) {
			var tag = p1Orient ? "p1Prize" : "p2Prize"
			addImageOfItem(deck[0], $("#" + tag), "", false)
			$("#p1DeckDescriptor").html("Cards left: " + deck.length);
		}
	}
	if ('HAND' in data) {
		var hand = data.HAND
		var loc = $(p1Orient ? "#p1HandPanel" : "#p2HandPanel")
		loc.empty()
		for (var i = 0; i < hand.length; i++) {
			var tag = (p1Orient ? "p1Hand" : "p2Hand") + i
			addCardToHand(hand[i], loc, tag, p1Orient)
			if (p1Orient) {
				$("#" + tag).click(function() {
					if ($(this).hasClass("noClick")) {
						$(this).removeClass("noClick")
					} else {
					showNonActionPopUp($(this).data("card_data"))	
					}
				})
				$("#" + tag).draggable({
					start: function (event, ui) {
						$(this).addClass("noClick")
					},
					//revert: true,
					cursor: 'move'
				})
			}
			
		}
	}
  if ('BENCH' in data) {
    var bench = data.BENCH
    for (var i = 0; i < bench.length; i++) {
      var tagWithoutIndex = p1Orient ? "p1Bench" : "p2Bench"
      var tag = tagWithoutIndex + (i+1)
      $("#" + tagWithoutIndex + "Display" + (i+1)).empty()
      $("#" + tagWithoutIndex + "Descriptor" + (i+1)).empty()
      $("#" + tagWithoutIndex + "EnergyIcon" + (i+1)).empty()
      if(!isPlaceholder(bench[i])) {
        addImageOfItem(bench[i], $("#" + tagWithoutIndex + "Display" + (i+1)), tagWithoutIndex + (i+1), true)
        energyDescription("#" + tagWithoutIndex + "EnergyIcon" + (i+1), bench[i])
        $("#" + tag).unbind('click')
        $("#" + tag).click(function() {
          if ($(this).hasClass("noClick")) {
            $(this).removeClass("noClick")
          } else {
            showPopUpActive($(this).data("card_data"), $(this).attr("id"))            
          }

		    })
        $("#" + tag).draggable({
          start: function (event, ui) {
            $(this).addClass("noClick")
          },
          //revert: true,
          cursor: 'move'
        })
        $("#" + tagWithoutIndex + "Descriptor" + (i+1)).html(bench[i].CURR_HP + "/" + bench[i].MAX_HP)
      }
    }
  }
	
	if ('ACTIVE' in data) {
		var active = data.ACTIVE
    var tag = p1Orient ? "p1Active" : "p2Active"
    $("#" + tag + "Display").empty()
      $("#" + tag + "Descriptor").empty()
      $("#" + tag + "EnergyIcon").empty()
		if (active.IDENTIFIER != "PLACEHOLDER") {
		  addImageOfItem(active, $("#" + tag + "Display"), tag, true)
      $("#" + tag + "Descriptor").html(populateDescriptor(active))
      energyDescription("#" + tag + "EnergyIcon", active)
      $("#" + tag).unbind('click')
      $("#" + tag).click(function() {
				if ($(this).hasClass("noClick")) {
					$(this).removeClass("noClick")
				} else {
				  showPopUpActive($(this).data("card_data"), $(this).attr("id"))
        }
		  })
		  $("#" + tag).draggable({
			  start: function (event, ui) {
				  $(this).addClass("noClick")
        },
			  //revert: true,
				cursor: 'move'
      })
		}
	}
}

function populateDescriptor(card) {
  var res = card.CURR_HP + "/" + card.MAX_HP
  if (card.POISON_STATUS != "None") {
    res += "<font color='purple'> &#9679;</font>"
  }
  return res
}

function energyDescription(tag, card) {
  var energyCnts = {colorless:0, fire:0, water:0, fighting:0, grass:0, psychic:0, thunder:0}
  var energyIcons = ""
  var fontTag = "<font color='[COLOR]'>&#9679;</font>"
  for (var i=0; i<card.ENERGY_CARDS.length; i++) {
    var energyCard = card.ENERGY_CARDS[i]
    switch (energyCard.ENERGY_TYPE) {
      case "COLORLESS": energyIcons += fontTag.replace("[COLOR]", "white"); break;
      case "FIRE": energyIcons += fontTag.replace("[COLOR]", "red"); break;
      case "WATER": energyIcons += fontTag.replace("[COLOR]", "blue"); break;
      case "FIGHTING": energyIcons += fontTag.replace("[COLOR]", "brown"); break;
      case "GRASS": energyIcons += fontTag.replace("[COLOR]", "green"); break;
      case "PSYCHIC": energyIcons += fontTag.replace("[COLOR]", "purple"); break;
      case "THUNDER": energyIcons += fontTag.replace("[COLOR]", "yellow"); break;
    }
    if (i < card.ENERGY_CARDS.length - 1) {
      energyIcons += "<br>"
    }
  }

  $(tag).html(energyIcons)
}

function handToActive(handIndex) {
  $("#content").data("socket").send("HAND_TO_ACTIVE<>" + handIndex)
}

function handToBench(handIndex, benchIndex) {
  $("#content").data("socket").send("HAND_TO_BENCH<>" + handIndex + "<>" + benchIndex)
}

function benchToBench(benchIndex1, benchIndex2) {
  $("#content").data("socket").send("BENCH_TO_BENCH<>" + benchIndex1 + "<>" + benchIndex2)
}

function benchToActive(benchIndex) {
	$("#content").data("socket").send("BENCH_TO_ACTIVE<>" + benchIndex)
}

function activeToBench(benchIndex) {
  $("#content").data("socket").send("ACTIVE_TO_BENCH<>" + benchIndex)
}

function isPlaceholder(item) {
	return item.IDENTIFIER == "PLACEHOLDER"
}

function addCardToHand(item, location, idName, showFront) {
	location.append("<div class=\"cardHandDisplay\">"
			+ imgTag.replace("[IMG_NAME]", showFront ? item.IMG_NAME : cardBackImg)
			        .replace("[ID]", idName)
			+ "</div>")
	$("#" + idName).data("card_data", item)
}

function addImageOfItem(item, location, idName, showFront) {
	if (!isPlaceholder(item)) {
		location.html(
			imgTag.replace("[IMG_NAME]", showFront ? item.IMG_NAME : cardBackImg)
			      .replace("[ID]", idName))
    $("#" + idName).data("card_data", item)
    $("#" + idName).effect("highlight", {color:"#669966"}, 3000)
	}
}

function closeUp(tag) {
  var popUpDiv = "<div>" +
      "<div class=\"row row-10-100\" style=\"background-color: green;\"></div>" +
      "<div class=\"row row-90-100\" style=\"background-color: purple;\">" +
        "<div class=\"col col-3-5\" style=\"background-color: red;\"></div>" +
        "<div class=\"col col-2-5\" style=\"background-color: blue;\"></div>" +
      "</div>" +
      "</div>"

  $(popUpDiv).dialog({
    height: 500,
    width: 350
  })
}

function showNonActionPopUp(item) {
	var popUp = "<div style=\"background-color: #888888;\">" +
	        "<div class=\"row row-1-10\"></div>" +
	        "<div class=\"row row-8-10\">" +
	          "<div class=\"col col-1-5\"></div>" +
	          "<div class=\"col col-3-5\">" +
	            imgTag.replace("[IMG_NAME]", item.IMG_NAME) +
	          "</div>" +
	          "<div class=\"col col-1-5\"></div>" +
	        "</div>" +
	        "<div class=\"row row-1-10\"></div>" +
	    "</div>"
    $(popUp).dialog({
    	modal: true,
        height: 400,
        width: 350,
        title: item.DISPLAY_NAME
    })
}

function energyDisplayStringForPopup(card) {
	var energyCnts = {colorless:0, fire:0, water:0, fighting:0, grass:0, psychic:0, thunder:0}
	for (var i=0; i<card.ENERGY_CARDS.length; i++) {
    	var energyCard = card.ENERGY_CARDS[i]
   	 	switch (energyCard.ENERGY_TYPE) {
      		case "COLORLESS": energyCnts.colorless += 1; break;
      		case "FIRE": energyCnts.fire += 1; break;
      		case "WATER": energyCnts.water += 1; break;
      		case "FIGHTING": energyCnts.fighting += 1; break;
      		case "GRASS": energyCnts.grass += 1; break;
     		  case "PSYCHIC": energyCnts.psychic += 1; break;
      		case "THUNDER": energyCnts.thunder += 1; break;
    	}
    }
	var res = "<table border=\"1\">" +
		"<tr>" +
	        "<td>" + energyTag.replace("[IMG_NAME]", "Lightning-Symbol.png") + "</td>" +
	              "<td>x" + energyCnts.thunder + "</td>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Fire-Symbol.png") + "</td>" +
	              "<td>x" + energyCnts.fire + "</td>" +
	            "</tr>" +
	            "<tr>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Water-Symbol.png") + "</td>" +
	              "<td>x" + energyCnts.water + "</td>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Grass-Symbol.png") + "</td>" +
	              "<td>x" + energyCnts.grass + "</td>" +
	            "</tr>" +
	            "<tr>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Fighting-Symbol.png") + "</td>" +
	              "<td>x" + energyCnts.fighting + "</td>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Psychic-Symbol.png") + "</td>" +
	              "<td>x" + energyCnts.psychic + "</td>" +
	            "</tr>" +
	            "<tr>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Colorless-Symbol.png") + "</td>" +
	              "<td>x" + energyCnts.colorless + "</td>" +
	              "<td></td>" +
	              "<td></td>" +
	            "</tr>" +
	            "</table>"
	return res
}

function useAttack(moveNum, tag) {
  var socket = $("#content").data("socket")
  if (tag.includes("Bench")) {
    var benchIndex = tag.charAt(tag.length - 1)
    socket.send("ATTACK_FROM_BENCH<>" + benchIndex + "<>" + moveNum)
  } else if (tag.includes("Active")) {
    socket.send("ATTACK_FROM_ACTIVE<>" + moveNum)
  }
	$(".popUp").dialog("close")
}

function drawMoveButtons(card, tag) {
	var res = ""
  for (var i=0; i<card.MOVES.length; i++) {
    if (!isPlaceholder(card.MOVES[i])) {
      var move = card.MOVES[i]
      var name = ""
      switch (move.MOVE_STATUS) {
        case "ACTIVATABLE":
          name = "<font style=\"color:red;\">Activate " + move.MOVE_NAME + "</font>"
          break;
        case "ACTIVATED":
          name = "<font style=\"color:red;\">Deactivate " + move.MOVE_NAME + "</font>"
          break;
        default:
          name = move.MOVE_NAME
          break;
      }
      var disabledStr = move.MOVE_STATUS == "DISABLED" ? "disabled" : ""
		  res += "<button class=\"actionButton\" " + disabledStr
		    + " onclick=\"useAttack('" + (i+1) + "', '" + tag + "');\">" + name + "</button><br><br>"
    }
  }
	return res
}

function statusConditionTag(item) {
  if (item.POISON_STATUS != "None") {
    return "<font style=\"color: purple;\">" + item.POISON_STATUS + "</font>"
  } else {
    return item.POISON_STATUS
  }
}

function showPopUpActive(item, tag) {
	var popUp = "<div style=\"background-color: #888888;\" class=\"popUp\">" +
	      "<div class=\"col col-3-5\">" +
	        "<div class=\"row row-1-10\"></div>" +
	        "<div class=\"row row-8-10\">" +
	          "<div class=\"col col-1-5\"></div>" +
	          "<div class=\"col col-3-5\">" +
	            imgTag.replace("[IMG_NAME]", item.IMG_NAME) +
	          "</div>" +
	          "<div class=\"col col-1-5\"></div>" +
	        "</div>" +
	        "<div class=\"row row-1-10\"></div>" +
	      "</div>" +
	      "<div class=\"col col-2-5\" style=\"color: white;\">" +
	        "<div class=\"row row-1-3\" style=\"font-size: 12px;\">" +
	            "<br><br><br>Current HP: " + item.CURR_HP + "/" + item.MAX_HP + "<br><br>" +
	            "Status Condition: " + statusConditionTag(item) + "<br><br>" +
	            "Other Conditions: None" +
	        "</div>" +
	        "<div class=\"row row-1-3\">" +
	            energyDisplayStringForPopup(item) +	            
	        "</div>" +
	        "<div class=\"row row-1-3\">" +
	        	drawMoveButtons(item, tag) +
	        "</div>" +
	      "</div>" +
	    "</div>"
    $(popUp).dialog({
    	modal: true,
        height: 400,
        width: 550,
        title: item.DISPLAY_NAME
    })
}
