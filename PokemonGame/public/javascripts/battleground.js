var cardBackImg = "Card-Back.jpg"
var srcTag = "/assets/images/[IMG_NAME]"
var imgTag = "<img src=" + srcTag + " height=\"100%\" width=\"100%\" id=[ID] dat=\"[DAT]\"></img>" 
var energyTag = "<img src=" + srcTag + " height=\"50%\" width=\"50%\" id=[ID] dat=\"[DAT]\"></img>" 

var popUpTag = "<img src=" + srcTag + " height=\"70%\" width=\"60%\" id=\"[ID]\" dat=\"[DAT]\">" 


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
					revert: true,
					cursor: 'move'
				})
			}
			
		}
	}
	
	if ('ACTIVE' in data) {
		var active = data.ACTIVE
		if (active.IDENTIFIER != "PLACEHOLDER") {
          var tag = p1Orient ? "p1Active" : "p2Active"
		  addImageOfItem(active, $("#" + tag), true)
          $("#" + tag + "Descriptor").html(active.CURR_HP + "/" + active.MAX_HP)
          energyDescription("#" + tag + "EnergyIcon", active)
          $("#" + tag).click(function() {
				showPopUpActive($(this).data("card_data"))
		  })
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
			       // .replace("[DAT]", JSON.stringify(item))
			+ "</div>")
	$("#" + idName).data("card_data", item)
}

function addImageOfItem(item, location, showFront) {
	if (!isPlaceholder(item)) {
		location.html(
			imgTag.replace("[IMG_NAME]", showFront ? item.IMG_NAME : cardBackImg),
			imgTag.replace("[ID]", location))
        $(location).data("card_data", item)
	}
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

function energyDisplayStringForPopup(item) {
	var res = "<table border=\"1\">" +
		"<tr>" +
	        "<td>" + energyTag.replace("[IMG_NAME]", "Lightning-Symbol.png") + "</td>" +
	              "<td>x2</td>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Fire-Symbol.png") + "</td>" +
	              "<td>x2</td>" +
	            "</tr>" +
	            "<tr>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Water-Symbol.png") + "</td>" +
	              "<td>x2</td>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Grass-Symbol.png") + "</td>" +
	              "<td>x2</td>" +
	            "</tr>" +
	            "<tr>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Fighting-Symbol.png") + "</td>" +
	              "<td>x2</td>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Psychic-Symbol.png") + "</td>" +
	              "<td>x2</td>" +
	            "</tr>" +
	            "<tr>" +
	              "<td>" + energyTag.replace("[IMG_NAME]", "Colorless-Symbol.png") + "</td>" +
	              "<td>x2</td>" +
	              "<td></td>" +
	              "<td></td>" +
	            "</tr>" +
	            "</table>"
	return res
}

function showPopUpActive(item) {
	var popUp = "<div style=\"background-color: #888888;\">" +
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
	      "<div class=\"col col-2-5\">" +
	        "<div class=\"row row-1-3\" style=\"background-color: green\">" +
	            "<br><br><br>Current HP: " + item.CURR_HP + "/" + item.MAX_HP + "<br>" +
	            "Status Condition: None <br>" +
	            "Other Conditions: None" +
	        "</div>" +
	        "<div class=\"row row-1-3\" style=\"background-color: red\">" +
	            energyDisplayStringForPopup(item) +	            
	        "</div>" +
	        "<div class=\"row row-1-3\" style=\"background-color: blue\">" +
	          "<button>Move One</button><br><br>" +
	          "<button>Move Two</button>" +
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