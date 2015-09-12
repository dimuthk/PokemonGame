package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.card.Card
import src.board.drag.CustomDragInterpreter
import src.board.intermediary._
import src.move.MoveBuilder._
import src.player.Player
import src.card.energy._
import src.card.Deck
import src.card.pokemon._

class Electrode extends StageOnePokemon(
    "Electrode",
    "Electrode-Base-Set-21.jpg",
    Deck.BASE_SET,
    Identifier.ELECTRODE,
    id = 101,
    maxHp = 80,
    firstMove = Some(new Buzzap()),
    secondMove = Some(new Move(
        "Electric Shock",
        3,
        Map(EnergyType.THUNDER -> 3)) {
            def perform = (owner, opp, args) => selfDamageChanceAttack(owner, opp, 50, 10)
    }),
    energyType = EnergyType.THUNDER,
    weakness = Some(EnergyType.FIGHTING),
    retreatCost = 1)

class Buzzap extends ActivePokemonPower(
    "Buzzap",
    dragInterpreter = Some(new BuzzapDrag())) {

  override def perform = (owner, opp, args) => togglePower()

}

class BuzzapSpecification(p : Player, eList : Seq[EnergyCard]) extends ClickableCardRequest(
  "Discard Energy",
  "Select the energy cards you want to discard.",
  p,
  1,
  eList)

private class BuzzapMasquerade(
    displayName : String,
    energyCount : Int,
    baseCard : Card) extends EnergyMasquerade(
        displayName,
        EnergyType.COLORLESS,
        energyCount,
        baseCard) {
        var set = false

        var realEtype = EnergyType.COLORLESS

        override def eType = realEtype
}

private class BuzzapDrag extends CustomDragInterpreter {

    val eTypes : Seq[EnergyCard] = List(
        new FireEnergy(),
        new WaterEnergy(),
        new ThunderEnergy(),
        new FightingEnergy(),
        new PsychicEnergy(),
        new GrassEnergy(),
        new DoubleColorlessEnergy())

    def findElectrode(owner : Player) : PokemonCard = owner.cardWithActivatedPower.get

    def discardCards(owner : Player) : EnergyMasquerade = {
        val electrode = findElectrode(owner)
        val cards = electrode.pickUp() diff List(electrode)
        if (owner.active == Some(electrode)) {
            owner.clearActive()
        } else {
            for (i <- 0 until 5) {
              if (owner.bench(i) == Some(electrode)) {
                owner.bench(i) = None
              }
            }
        }
        owner.garbage = owner.garbage ++ cards
        return new BuzzapMasquerade(
            "",
            2,
            electrode)
    }

    def assignBuzzapType(pc : PokemonCard, args : Seq[String]) : Option[IntermediaryRequest] = {
        val index = args(0).toInt
        val eType = eTypes(index).eType
        pc.energyCards.foreach {
            case b : BuzzapMasquerade => {
                if (!b.set) {
                    b.realEtype = eType
                    b.set = true
                }
            }
            case _ => ()
        }
        return None
    }

    def benchToBench = (owner, _, _, benchIndex1, benchIndex2, args) => args.length match {
        case 0 => {
            owner.bench(benchIndex1).get == findElectrode(owner) && owner.bench(benchIndex2).isDefined match {
                case true => Some(new BuzzapSpecification(owner, eTypes))
                case false => None
            }
        }
        case _ => {
            val buzzap = discardCards(owner)
            owner.bench(benchIndex2).get.attachEnergy(buzzap)
            assignBuzzapType(owner.bench(benchIndex2).get, args)
        }
    }

    def benchToActive = (owner, _, _, benchIndex, args) => args.length match {
        case 0 => {
            owner.bench(benchIndex).get == findElectrode(owner) && owner.active.isDefined match {
                case true => Some(new BuzzapSpecification(owner, eTypes))
                case false => None
            }
        }
        case _ => {
            val buzzap = discardCards(owner)
            owner.active.get.attachEnergy(buzzap)
            assignBuzzapType(owner.active.get, args)
        }
    }

    def activeToBench = (owner, _, _, benchIndex, args) => args.length match {
        case 0 => {
            owner.active.get == findElectrode(owner) && owner.bench(benchIndex).isDefined match {
                case true => Some(new BuzzapSpecification(owner, eTypes))
                case false => None
            }
        }
        case _ => {
            val buzzap = discardCards(owner)
            owner.bench(benchIndex).get.attachEnergy(buzzap)
            assignBuzzapType(owner.bench(benchIndex).get, args)
        }
    }

    def handToActive = (_, _, _, _, _) => None

    def handToBench = (_, _, _, _, _, _) => None

}