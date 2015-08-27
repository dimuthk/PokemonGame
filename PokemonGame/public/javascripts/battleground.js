var cardBackImg = "Card-Back.jpg"
var srcTag = "/assets/images/[IMG_NAME]"
var imgTag = "<img src=" + srcTag + " height=\"100%\" width=\"100%\" id=[ID] dat=\"[DAT]\"></img>" 
var energyTag = "<img src=" + srcTag + " height=\"50%\" width=\"50%\" id=[ID] dat=\"[DAT]\"></img>" 

var popUpTag = "<img src=" + srcTag + " height=\"70%\" width=\"60%\" id=\"[ID]\" dat=\"[DAT]\">" 

var intermediaryCardHolder = "<div style=\" white-space: nowrap; padding: 1%; float: left; position: relative; width: 30%; height: 100%;\">" + imgTag + "\" </div>"


function establishConnection() {
	var socket = new WebSocket("ws://localhost:9000/socket")

	$("#content").data("socket", socket)

	socket.onopen = function() {
		socket.send("BUILD_MACHOP")
	}

	socket.onmessage = function (event) {
    var data = JSON.parse(event.data)
		repaintBoard(data, socket)
    processIntermediary(data)
	}

}

function processIntermediary(data) {
  if (!isPlaceholder(data.intermediary)) {
    showClickableCardRequest(data.intermediary)
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
        switch (card.ENERGY_TYPE) {
          case "GRASS":
            $("#content").animate({backgroundColor: "green"}, 2000);
            break;
          case "WATER":
            $("#content").animate({backgroundColor: "blue"}, 2000);
            break;
          default: break;
        }
        return true
      }
    }
  }
  return false
}

function repaintForPlayer(player, p1Orient) {
  var isTurn = player.IS_TURN
  $("#" + (p1Orient ? "p1TurnMarker" : "p2TurnMarker")).css("display", (player.IS_TURN ? "block" : "none"))
  var notification = player.NOTIFICATION
  var notifyTag = p1Orient ? "p1Notification" : "p2Notification"
  $("#" + notifyTag).empty()
  $("#" + notifyTag).css({opacity: 100})
  if (!isPlaceholder(notification)) {
    $("#" + notifyTag).html(notification)
    $("#" + notifyTag).animate({opacity: 0}, 1000)
  }

  var deck = player.DECK
  var deckTag = p1Orient ? "p1Deck" : "p2Deck"
  $("#" + deckTag + "Descriptor").empty()
  if (deck.length > 0) {
    $("#" + deckTag + "Descriptor").html("Cards left: " + deck.length) 
    processCard(deck[0], deckTag + "Display", deckTag, false)
  }

  var garbage = player.GARBAGE
  var garbageTag = p1Orient ? "p1Garbage" : "p2Garbage"
  $("#" + garbageTag + "Display").empty()
  if (garbage.length > 0) {
    processCard(garbage[garbage.length] - 1, garbageTag + "Display", garbageTag, false)
  }

  var prizes = player.PRIZES
  var prizeTag = p1Orient ? "p1Prize" : "p2Prize"
  for (var i = 0; i < prizes.length; i++) {
    if (!isPlaceholder(prizes[i])) {
      processCard(prizes[i], prizeTag + "Display" + i, prizeTag + i, false)
    }
  }

  var hand = player.HAND
  var handPanelTag = p1Orient ? "p1HandPanel" : "p2HandPanel"
  $("#" + handPanelTag).empty()
  for (var i = 0; i < hand.length; i++) {
    var handTag = (p1Orient ? "p1Hand" : "p2Hand") + i
    processCard(hand[i], handPanelTag, handTag, true)
  }

  var bench = player.BENCH
  var benchTag = p1Orient ? "p1Bench" : "p2Bench"
  for (var i = 0; i < bench.length; i++) {
    $("#" + benchTag + "Display" + (i+1)).empty()
    $("#" + benchTag + "Descriptor" + (i+1)).empty()
    $("#" + benchTag + "EnergyIcon" + (i+1)).empty()
    if (!isPlaceholder(bench[i])) {
      processCard(bench[i], benchTag + "Display" + (i+1), benchTag + (i+1), false)
      energyDescription("#" + benchTag + "EnergyIcon" + (i+1), bench[i])
      $("#" + benchTag + "Descriptor" + (i+1)).html(populateDescriptor(bench[i]))
    }
  }
	
  var active = player.ACTIVE
  var activeTag = p1Orient ? "p1Active" : "p2Active"
  $("#" + activeTag + "Display").empty()
  $("#" + activeTag + "Descriptor").empty()
  $("#" + activeTag + "EnergyIcon").empty()
  if (!isPlaceholder(active)) {
    processCard(active, activeTag + "Display", activeTag, false)
    energyDescription("#" + activeTag + "EnergyIcon", active)
    $("#" + activeTag + "Descriptor").html(populateDescriptor(active))
  }
}

function addCardToDisplay(card, displayTag, imageTag) {
  $("#" + displayTag).html(
    imgTag.replace("[IMG_NAME]", card.FACE_UP ? card.IMG_NAME : cardBackImg)
          .replace("[ID]", imageTag))
  $("#" + imageTag).data("card_data", card)
}

function addCardToHand(card, handPanelTag, imageTag) {
  $("#" + handPanelTag).append("<div class=\"cardHandDisplay\">"
      + imgTag.replace("[IMG_NAME]", card.FACE_UP ? card.IMG_NAME : cardBackImg)
              .replace("[ID]", imageTag)
      + "</div>")
  $("#" + imageTag).data("card_data", card)
}

function processCard(card, displayTag, imageTag, forHand) {
  if (forHand) {
    addCardToHand(card, displayTag, imageTag)
  } else {
    addCardToDisplay(card, displayTag, imageTag)    
  }
  $("#" + imageTag).unbind('click')
  if (card.DRAGGABLE) {
    $("#" + imageTag).draggable({
        start: function (event, ui) {
          $(this).addClass("noClick")
        },
        cursor: 'move'
      })
  }
  if (card.CLICKABLE) {
    $("#" + imageTag).click(function() {
      if ($(this).hasClass("noClick")) {
        $(this).removeClass("noClick")
      } else if (card.USABLE) {
        showPopUpActive($(this).data("card_data"), $(this).attr("id"))
      } else {
        showNonActionPopUp($(this).data("card_data"))
      }
    })
  }
}

function populateDescriptor(card) {
  var res = card.CURR_HP + "/" + card.MAX_HP
  if (card.POISON_STATUS != "None") {
    res += "<font color='purple'> &#9679;</font>"
  }
  if (card.STATUS_CONDITION != "None") {
    res += "<font color='yellow'> &#9679;</font>"
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
  $("#content").data("socket").send("DRAG<>HAND_TO_ACTIVE<>" + handIndex)
}

function handToBench(handIndex, benchIndex) {
  $("#content").data("socket").send("DRAG<>HAND_TO_BENCH<>" + handIndex + "<>" + benchIndex)
}

function benchToBench(benchIndex1, benchIndex2) {
  $("#content").data("socket").send("DRAG<>BENCH_TO_BENCH<>" + benchIndex1 + "<>" + benchIndex2)
}

function benchToActive(benchIndex) {
	$("#content").data("socket").send("DRAG<>BENCH_TO_ACTIVE<>" + benchIndex)
}

function activeToBench(benchIndex) {
  $("#content").data("socket").send("DRAG<>ACTIVE_TO_BENCH<>" + benchIndex)
}

function isPlaceholder(item) {
	return item.IDENTIFIER == "PLACEHOLDER"
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
    socket.send("MOVE<>ATTACK_FROM_BENCH<>" + benchIndex + "<>" + moveNum)
  } else if (tag.includes("Active")) {
    socket.send("MOVE<>ATTACK_FROM_ACTIVE<>" + moveNum)
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
        case "PASSIVE":
          name = "<font style=\"color:blue;\">" + move.MOVE_NAME + "</font>"
          break;
        default:
          name = move.MOVE_NAME
          break;
      }
      var disabledStr = (move.MOVE_STATUS == "DISABLED" || move.MOVE_STATUS == "PASSIVE") ? "disabled" : ""
		  res += "<button class=\"actionButton\" " + disabledStr
		    + " onclick=\"useAttack('" + (i+1) + "', '" + tag + "');\">" + name + "</button><br><br>"
    }
  }
	return res
}

function statusConditionTag(item) {
  if (item.POISON_STATUS != "None") {
    return "<font style=\"color: purple;\">" + item.POISON_STATUS + "</font>"
  } else if (item.STATUS_CONDITION != "None") {
    return "<font style=\"color: yellow;\">" + item.STATUS_CONDITION + "</font>"
  } else {
    return item.POISON_STATUS
  }
}

function generalConditionTag(item) {
  if (item.GENERAL_CONDITION != "None") {
    return "<font style=\"color: blue;\">" + item.GENERAL_CONDITION + "</font>"
  } else {
    return item.GENERAL_CONDITION
  }
}

function generateCardList(intermediary) {
  var cardList = ""
  for (var i=0; i<intermediary.CARD_LIST.length; i++) {
    var card = intermediary.CARD_LIST[i]
    cardList += intermediaryCardHolder.replace("[IMG_NAME]", card.FACE_UP ? card.IMG_NAME : cardBackImg)
  }
  return cardList
  /*$("#").append("<div class=\"cardHandDisplay\">"
      + imgTag.replace("[IMG_NAME]", card.FACE_UP ? card.IMG_NAME : cardBackImg)
              .replace("[ID]", imageTag)
      + "</div>")
  $("#" + imageTag).data("card_data", card)*/

}

function showClickableCardRequest(intermediary) {
  var popUp = "<div style=\"background-color: #888888;\">" +
          "<div class=\"row row-1-5\">" +
          intermediary.REQUEST_MSG +
          "</div>" +
          "<div class=\"row row-3-5\">" +
            "<div class=\"col col-1-10\"></div>" +
            "<div class=\"col col-8-10\" style=\" background-color: blue; overflow-y: hidden; overflow-x : scroll;\">" +
              generateCardList(intermediary) +
            "</div>" +
            "<div class=\"col col-1-10\"></div>" +
          "</div>" +
          "<div class=\"row row-1-5\"></div>" +
      "</div>"
    $(popUp).dialog({
      modal: true,
        height: 400,
        width: 800,
        title: intermediary.REQUEST_TITLE
    })
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
	            "Other Conditions: " + generalConditionTag(item) +
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
