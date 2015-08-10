package card

import json.Jsonable

abstract class Card(val displayName : String, val imageName : String) extends Jsonable