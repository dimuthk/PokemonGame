var cardBackImg = "Card-Back.jpg"
var srcTag = "/assets/images/[IMG_NAME]"
var imgTag = "<img src=" + srcTag + " height=\"100%\" width=\"100%\" id=[ID] dat=\"[DAT]\"></img>" 
var energyTag = "<img src=" + srcTag + " height=\"50%\" width=\"50%\" id=[ID] dat=\"[DAT]\"></img>" 

var popUpTag = "<img src=" + srcTag + " height=\"70%\" width=\"60%\" id=\"[ID]\" dat=\"[DAT]\">" 

var intermediaryCardHolder = "<div class=\"intermediary\" style=\"  padding: 1%; display: inline-block; position: relative; width: 30%; height: 90%;\">" + imgTag + "\" </div>"


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
    switch (data.intermediary.IDENTIFIER) {
      case "CLICK_CARD_REQUEST":
        showClickableCardRequest(data.intermediary)
        break;
      case "SPECIFIC_CLICK_CARD_REQUEST":
        showSpecificClickableCardRequest(data.intermediary)
        break;
      case "SINGLE_DISPLAY":
        showSingleDisplay(data.intermediary)
        break;
      case "OPPONENT_CARD_INTERFACE":
        showOpponentCardInterface(data.intermediary)
        break;
    }
  }
}

/**
 * Updates the board given the new set of JSON values returned from the server.
 */
function repaintBoard(data, socket) {
	repaintForPlayer(data.player1, true)
	repaintForPlayer(data.player2, false)
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
          case "FIGHTING":
            $("#content").animate({backgroundColor: "#A52A2A"}, 2000);
            break;
          case "PSYCHIC":
            $("#content").animate({backgroundColor: "purple"}, 2000);
            break;
          case "THUNDER":
            $("#content").animate({backgroundColor: "yellow"}, 2000);
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

  var pNum = p1Orient ? 1 : 2
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

  for (var i = 1; i < 6; i++) {
    $("#" + benchTag + "Display" + i).droppable({
        accept: "*",
        drop : function(event, ui) {
          var tag = $(ui.draggable).attr("id")
          var benchTag = $(this).attr("id")
          var benchIndex = benchTag.charAt(benchTag.length - 1)
          if (tag.includes("Active")) {
            // active to bench
            activeToBench(pNum, benchIndex)
          } else if (tag.includes("Bench")) {
            // bench to bench
            var benchIndex2 = tag.charAt(tag.length - 1)
            benchToBench(pNum, benchIndex2, benchIndex)
          } else if (tag.includes("Hand")) {
            // hand to bench
            var handIndex = tag.charAt(tag.length - 1)
            handToBench(pNum, handIndex, benchIndex)
          }
          
        }
    })
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

  $("#" + activeTag + "Display").droppable({
    accept: "*",
    drop : function(event, ui) {
      var tag = $(ui.draggable).attr("id")
      if (tag.includes("Active")) {
            // active to active
      } else if (tag.includes("Bench")) {
            // bench to active
        var benchIndex = tag.charAt(tag.length - 1)
        benchToActive(pNum, benchIndex)
      } else if (tag.includes("Hand")) {
            // hand to active
        var handIndex = tag.charAt(tag.length - 1)
        handToActive(pNum, handIndex)
      }
    }
  })
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
      } else if (card.DISPLAYABLE || card.USABLE) {
        showPopUpActive($(this).data("card_data"), $(this).attr("id"))
      } else {
        showNonActionPopUp($(this).data("card_data"), $(this).attr("id"))
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

function handToActive(pNum ,handIndex) {
  $("#content").data("socket").send("DRAG<>HAND_TO_ACTIVE<>" + pNum + "<>" + handIndex)
}

function handToBench(pNum, handIndex, benchIndex) {
  $("#content").data("socket").send("DRAG<>HAND_TO_BENCH<>" + pNum + "<>" + handIndex + "<>" + benchIndex)
}

function benchToBench(pNum, benchIndex1, benchIndex2) {
  $("#content").data("socket").send("DRAG<>BENCH_TO_BENCH<>" + pNum + "<>" + benchIndex1 + "<>" + benchIndex2)
}

function benchToActive(pNum, benchIndex) {
	$("#content").data("socket").send("DRAG<>BENCH_TO_ACTIVE<>" + pNum + "<>" + benchIndex)
}

function activeToBench(pNum, benchIndex) {
  $("#content").data("socket").send("DRAG<>ACTIVE_TO_BENCH<>" + pNum + "<>" + benchIndex)
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
  var pNum = tag.includes("p1") ? 1 : 2
  if (tag.includes("Bench")) {
    var benchIndex = tag.charAt(tag.length - 1)
    socket.send("MOVE<>ATTACK_FROM_BENCH<>" + pNum +"<>" + benchIndex + "<>" + moveNum)
  } else if (tag.includes("Active")) {
    socket.send("MOVE<>ATTACK_FROM_ACTIVE<>" + pNum +"<>" + moveNum)
  } else if (tag.includes("Deck")) {
    socket.send("MOVE<>ATTACK_FROM_DECK<>" + pNum + "<>" + moveNum)
  } else if (tag.includes("Hand")) {
    var handIndex = tag.charAt(tag.length - 1)
    socket.send("MOVE<>ATTACK_FROM_HAND<>" + pNum + "<>" + handIndex + "<>" + moveNum)
  } else if (tag.includes("Prize")) {
    var prizeIndex = tag.charAt(tag.length - 1)
    socket.send("MOVE<>ATTACK_FROM_PRIZE<>" + pNum + "<>" + prizeIndex + "<>" + moveNum)
  }
	$(".popUp").dialog("close")
}

function drawMoveButtons(card, tag) {
  if (!card.USABLE) {
    return ""
  }
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
		  res += "<button style=\"width: 100%; left: -15%; top: -20%; position: relative;\" class=\"actionButton\" " + disabledStr
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
  if (!isPlaceholder(item.GENERAL_CONDITION)) {
    return "<font style=\"color: blue;\">" + item.GENERAL_CONDITION + "</font>"
  } else {
    return "None"
  }
}

function generateCardList(intermediary, forceCardUp) {
  forceCardUp = true
  var cardList = ""
  for (var i=0; i<intermediary.CARD_LIST.length; i++) {
    var card = intermediary.CARD_LIST[i]
    cardList += intermediaryCardHolder.replace("[IMG_NAME]", (card.FACE_UP || forceCardUp) ? card.IMG_NAME : cardBackImg)
         .replace("[ID]", "\"intermediary" + i + "\"")
  }
  return cardList
}

function handleClickable(clickedCard) {
  var intermediary = $("#intermediaryPanel").data("intermediary")
  var clickReq = intermediary.CLICK_COUNT
  var clickCount = $("#intermediaryPanel").data("clickCount")
  if ($(clickedCard).hasClass('selected')) {
    $(clickedCard).removeClass('selected')
    clickCount--
  } else {
    if (clickCount < clickReq) {
      $(clickedCard).addClass('selected')
      clickCount++
    }
  }
  $("#intermediaryPanel").data("clickCount", clickCount)
  if (clickCount == clickReq) {
    $(".submitIntermediary").prop("disabled", false)
  } else {
    $(".submitIntermediary").prop("disabled", true)
  }
}

function sendClickIntermediary() {
  var indices = ""
  for (var i=0; i<$("#intermediaryPanel").data("numCards"); i++) {
    if ($("#intermediary" + i).hasClass('selected')) {
      indices += i + ","
    }
    $("#intermediary" + i).removeAttr('id')
  }
  indices = indices.substring(0, indices.length - 1)
  var tag = $("#intermediaryPanel").data("intermediary").SERVER_TAG
  tag += indices
  $("#content").data("socket").send(tag)
  $(".popUp").dialog("close")
}


function contains(matcher, value) {
  for (var i=0; i<matcher.length; i++) {
    if (matcher[i] == value) {
      return true
    }
  }
  return false
}

function showSpecificClickableCardRequest(intermediary) {
    var popUp = "<div style=\"background-color: #888888;\" class=\"popUp\">" +
          "<div class=\"row row-1-5\">" +
            "<div style=\"text-align: center; top: 10%; position: relative; font-size:15px; color: white;\">" + intermediary.REQUEST_MSG + "</div>" +
          "</div>" +
          "<div class=\"row row-3-5\">" +
            "<div class=\"col col-1-10\"></div>" +
            "<div class=\"col col-8-10\" id=\"intermediaryPanel\" style=\" background-color: #484848; border-radius: 10px; overflow-y: hidden; white-space: nowrap; overflow-x : scroll;\">" +
              generateCardList(intermediary, true) +
            "</div>" +
            "<div class=\"col col-1-10\"></div>" +
          "</div>" +
          "<div class=\"row row-1-5\">" +
            "<div class=\"col col-1-3\"></div>" +
            "<div class=\"col col-1-3\">" +
            "<button class=\"actionButton submitIntermediary\" onclick=sendClickIntermediary() disabled  style=\"top: 20%; width: 100%; position: relative;\">Submit</button>" +
            "</div>" +
            "<div class=\"col col-1-3\"></div>" +
          "</div>" +
      "</div>"
    $(popUp).dialog({
      modal: true,
        height: 400,
        width: 800,
        title: intermediary.REQUEST_TITLE
    })
    $("#intermediaryPanel").data("clickCount", 0)
    $("#intermediaryPanel").data("intermediary", intermediary)
    $("#intermediaryPanel").data("numCards", intermediary.CARD_LIST.length)

    for (var i=0; i<intermediary.CARD_LIST.length; i++) {
      $("#intermediary" + i).unbind('click')
      if (intermediary.MATCHER[i] == true) {
        $("#intermediary" + i).click(function() {
          handleClickable(this)
        })
      }
    }
  }

  function showSingleDisplay(intermediary) {
    var popUp = "<div style=\"background-color: #888888;\" class=\"popUp\">" +
          "<div class=\"row row-1-5\">" +
            "<div style=\"text-align: center; top: 10%; position: relative; font-size:15px; color: white;\">" + intermediary.REQUEST_MSG + "</div>" +
          "</div>" +
          "<div class=\"row row-3-5\">" +
            "<div class=\"col col-1-10\"></div>" +
            "<div class=\"col col-8-10\">" +
              imgTag.replace("[IMG_NAME]", intermediary.DISPLAY_CARD.IMG_NAME) +
            "</div>" +
            "<div class=\"col col-1-10\"></div>" +
          "</div>" +
          "<div class=\"row row-1-5\">" +
            "<div class=\"col col-1-3\"></div>" +
            "<div class=\"col col-1-3\">" +
            "</div>" +
            "<div class=\"col col-1-3\"></div>" +
          "</div>" +
      "</div>"
    $(popUp).dialog({
      modal: true,
        height: 400,
        width: 300,
        title: intermediary.REQUEST_TITLE
    })
  }

function drawMirrorMoveButtons(card, tag) {
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
      res += "<button style=\"width: 100%; left: -15%; top: -20%; position: relative;\" class=\"actionButton\" " + disabledStr
        + " onclick=\"sendOpponentCardInterfaceIntermediary('" + (i+1) + "');\">" + name + "</button><br><br>"
    }
  }
  return res
}

function sendOpponentCardInterfaceIntermediary(index) {
  var intermediary = $("#intermediaryPanel").data("intermediary")
  var tag = intermediary.SERVER_TAG + index
  $("#content").data("socket").send(tag)
  $(".popUp").dialog("close")
}

function showOpponentCardInterface(intermediary, tag) {
  var popUp = "<div style=\"background-color: #888888;\" class=\"popUp\">" +
        "<div class=\"col col-3-5\">" +
          "<div class=\"row row-1-10\"></div>" +
          "<div class=\"row row-8-10\">" +
            "<div class=\"col col-1-5\"></div>" +
            "<div class=\"col col-3-5\">" +
              imgTag.replace("[IMG_NAME]", intermediary.DISPLAY_CARD.IMG_NAME) +
            "</div>" +
            "<div class=\"col col-1-5\"></div>" +
          "</div>" +
          "<div class=\"row row-1-10\"></div>" +
        "</div>" +
        "<div class=\"col col-2-5\" style=\"color: white;\">" +
            "<div class\"row row-1-3\" style=\"font-size: 12px;\">" +
            "</div>" +
          "<div class=\"row row-1-3\">" +            
          "</div>" +
          "<div class=\"row row-1-3\" id=\"intermediaryPanel\">" +
            drawMirrorMoveButtons(intermediary.DISPLAY_CARD, tag) +
          "</div>" +
        "</div>" +
      "</div>"
    $(popUp).dialog({
      modal: true,
        height: 400,
        width: 550,
        title: intermediary.DISPLAY_CARD.DISPLAY_NAME
    })

    $("#intermediaryPanel").data("intermediary", intermediary)
}

function showClickableCardRequest(intermediary) {
  var popUp = "<div style=\"background-color: #888888;\" class=\"popUp\">" +
          "<div class=\"row row-1-5\">" +
            "<div style=\"text-align: center; top: 10%; position: relative; font-size:15px; color: white;\">" + intermediary.REQUEST_MSG + "</div>" +
          "</div>" +
          "<div class=\"row row-3-5\">" +
            "<div class=\"col col-1-10\"></div>" +
            "<div class=\"col col-8-10\" id=\"intermediaryPanel\" style=\" background-color: #484848; border-radius: 10px; overflow-y: hidden; white-space: nowrap; overflow-x : scroll;\">" +
              generateCardList(intermediary) +
            "</div>" +
            "<div class=\"col col-1-10\"></div>" +
          "</div>" +
          "<div class=\"row row-1-5\">" +
            "<div class=\"col col-1-3\"></div>" +
            "<div class=\"col col-1-3\">" +
            "<button class=\"actionButton submitIntermediary\" onclick=sendClickIntermediary() disabled  style=\"top: 20%; width: 100%; position: relative;\">Submit</button>" +
            "</div>" +
            "<div class=\"col col-1-3\"></div>" +
          "</div>" +
      "</div>"
    $(popUp).dialog({
      modal: true,
        height: 400,
        width: 800,
        title: intermediary.REQUEST_TITLE
    })
    $("#intermediaryPanel").data("clickCount", 0)
    $("#intermediaryPanel").data("intermediary", intermediary)
    $("#intermediaryPanel").data("numCards", intermediary.CARD_LIST.length)

    for (var i=0; i<intermediary.CARD_LIST.length; i++) {
      $("#intermediary" + i).unbind('click')
      $("#intermediary" + i).click(function() {
        handleClickable(this)
      })
    }
}

function showNonActionPopUp(item, tag) {
  var popUp = "<div style=\"background-color: #888888;\">" +
          "<div class=\"row row-1-10\"></div>" +
          "<div class=\"row row-8-10\">" +
            "<div class=\"col col-1-5\"></div>" +
            "<div class=\"col col-3-5\">" +
              imgTag.replace("[IMG_NAME]", item.IMG_NAME) +
            "</div>" +
            "<div class=\"col col-1-5\"></div>" +
          "</div>" +
          "<div class=\"row row-1-10\">" +
            drawMoveButtons(item, tag) +
          "</div>" +
      "</div>"
    $(popUp).dialog({
      modal: true,
        height: 400,
        width: 350,
        title: item.DISPLAYABLE ? (item.DISPLAY_NAME) : ""
    })
}

function showPopUpActive(item, tag) {
	var popUp = "<div style=\"background-color: #888888;\" class=\"popUp\">" +
	      "<div class=\"col col-3-5\">" +
	        "<div class=\"row row-1-10\"></div>" +
	        "<div class=\"row row-8-10\">" +
	          "<div class=\"col col-1-5\"></div>" +
	          "<div class=\"col col-3-5\">" +
	            imgTag.replace("[IMG_NAME]", item.FACE_UP ? item.IMG_NAME : cardBackImg) +
	          "</div>" +
	          "<div class=\"col col-1-5\"></div>" +
	        "</div>" +
	        "<div class=\"row row-1-10\"></div>" +
	      "</div>" +
	      "<div class=\"col col-2-5\" style=\"color: white;\">" +
            "<div class\"row row-1-3\" style=\"font-size: 12px;\">" +
	             (item.DISPLAYABLE ? ("<br><br>Current HP: " + item.CURR_HP + "/" + item.MAX_HP + "<br><br>" +
	             "Status Condition: " + statusConditionTag(item) + "<br><br>" +
	             "Other Conditions: " + generalConditionTag(item) + "<br><br>") : "") +
	          "</div>" +
	        "<div class=\"row row-1-3\">" +
	            (item.DISPLAYABLE ? energyDisplayStringForPopup(item) : "") +	            
	        "</div>" +
	        "<div class=\"row row-1-3\">" +
	        	(item.CLICKABLE ? drawMoveButtons(item, tag) : "") +
	        "</div>" +
	      "</div>" +
	    "</div>"
    $(popUp).dialog({
    	modal: true,
        height: 400,
        width: 550,
        title: item.DISPLAYABLE ? item.DISPLAY_NAME : ""
    })
}
