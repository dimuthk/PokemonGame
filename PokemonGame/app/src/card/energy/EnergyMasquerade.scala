package src.card.energy

import src.card.Card
import src.card.Deck
import src.json.Identifier

import play.api.libs.json._

class EnergyMasquerade(
    displayName : String,
    eType : EnergyType.Value,
    energyCount : Int,
    val baseCard : Card) extends EnergyCard(
        displayName,
        baseCard.imgName,
        baseCard.getIdentifier(),
        eType,
        energyCount,
        baseCard.deck)