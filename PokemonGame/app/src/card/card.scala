package src.card

import src.json.Jsonable

/**
 * Base class for all cards used in the game.
 */
abstract class Card(val displayName : String, val imgName : String, val deck : Deck.Value) extends Jsonable