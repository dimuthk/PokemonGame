package card.pokemon

import org.json.JSONObject

import json.JSONIdentifier

class Machop extends PokemonCard(
    "Machop",
    "IMG",
    50){
  
  override def getIdentifier() = JSONIdentifier.MACHOP
  
}