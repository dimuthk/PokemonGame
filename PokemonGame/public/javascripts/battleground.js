var cardBackImg = "Card-Back.jpg"
var srcTag = "/assets/images/[IMG_NAME]"
var imgTag = "<img src=" + srcTag + " height=\"100%\" width=\"100%\" id=[ID]>" 

function establishConnection() {
	var socket = new WebSocket("ws://localhost:9000/socket")

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
	
	$("#p1Active").droppable({
				accept: "*",
				drop: function(event, ui) {
					handToActive(event, ui, $(this), socket)
				} });
}

function repaintForPlayer(data, p1Orient) {
	if ('PRIZES' in data) {
		var prizes = data.PRIZES
		for (var i = 0; i < prizes.length; i++) {
			var tag = (p1Orient ? "p1Prize" : "p2Prize") + (i+1)
			addImageOfItem(prizes[i], $("#" + tag), false)
		}
	}
	if ('DECK' in data) {
		var deck = data.DECK
		if (deck.length > 0) {
			var tag = p1Orient ? "p1Prize" : "p2Prize"
			addImageOfItem(deck[0], $("#" + tag), false)
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
			$("#" + tag).draggable({revert: true, cursor: 'move'})
		}
	}
	
	if ('ACTIVE' in data) {
		var active = data.ACTIVE
		if (active.IDENTIFIER != "PLACEHOLDER") {
          var tag = p1Orient ? "p1Active" : "p2Active"
		  addImageOfItem(active, $("#" + tag), true)
      $("#" + tag + "Descriptor").html(active.CURR_HP + "/" + active.MAX_HP)
      //$("#" + tag + "EnergyIcon").html(imgTag.replace("[IMG_NAME]", "Fire-Symbol.png") + imgTag.replace("[IMG_NAME]", "Fire-Symbol.png"))
      energyDescription("#" + tag + "EnergyIcon", active)
      //$("#" + tag + "EnergyDescriptor").html("<font color='white'>x1<br>x2</font>")
		}
	}
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
  //$(tag).html("<font color='red'>&#9679;<br>&#9679;</font>")
  /*$(tag).html(
    imgTag.replace("[IMG_NAME]", "Colorless-Symbol.png") +
    imgTag.replace("[IMG_NAME]", "Fire-Symbol.png") +
    imgTag.replace("[IMG_NAME]", "Water-Symbol.png") +
    imgTag.replace("[IMG_NAME]", "Fighting-Symbol.png") +
    imgTag.replace("[IMG_NAME]", "Grass-Symbol.png") +
    imgTag.replace("[IMG_NAME]", "Psychic-Symbol.png") +
    imgTag.replace("[IMG_NAME]", "Lightning-Symbol.png"))*/
}


function handToActive(event, ui, handItem, socket) {
	socket.send("HAND_TO_ACTIVE<>" + $(ui.draggable).attr("id"))
}

function isPlaceholder(item) {
	return item.IDENTIFIER == "PLACEHOLDER"
}

function addCardToHand(item, location, idName, showFront) {
	location.append("<div class=\"cardHandDisplay\">"
			+ imgTag.replace("[IMG_NAME]", showFront ? item.IMG_NAME : cardBackImg)
			        .replace("[ID]", idName)
			+ "</div>")
}

function addImageOfItem(item, location, showFront) {
	if (!isPlaceholder(item)) {
		location.html(imgTag.replace("[IMG_NAME]", showFront ? item.IMG_NAME : cardBackImg))
	}
}
