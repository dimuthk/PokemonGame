package src.board.state

import src.card.Card
import src.card.pokemon.PokemonCard
import src.player.Player
import src.board.InterpreterCommand

sealed abstract class StateCommand extends InterpreterCommand

abstract class ActiveOrBench(pc : PokemonCard) extends StateCommand

case class Active(pc : PokemonCard) extends ActiveOrBench(pc)

case class Bench(pc : PokemonCard) extends ActiveOrBench(pc)

case class Hand(pc : Card) extends StateCommand

case class Prize(pc : Card) extends StateCommand

case class Deck(pc : Card) extends StateCommand