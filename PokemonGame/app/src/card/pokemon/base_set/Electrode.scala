package src.card.pokemon.base_set

import src.json.Identifier
import src.move._
import src.card.Card
import src.board.drag._
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
        val cards = owner.pickUpCard(electrode) diff List(electrode)
        owner.discardCards(cards)
        return new BuzzapMasquerade("", 2, electrode)
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

    private def checkNotAdditional(owner : Player, drag : Option[PokemonCard], drop : Option[PokemonCard]) : Unit =
        if (drag.get == findElectrode(owner) && drop.isDefined) {
            throw new Exception("Got a buzzap request without additional info")
    }

    private def attachElectrode(owner : Player, drop : Option[PokemonCard], args : Seq[String]) : Unit = {
        val buzzap = discardCards(owner)
        drop.get.attachEnergy(buzzap)
        assignBuzzapType(drop.get, args)
    }

    override def handleCommand = (pData, dragCmd, args) => dragCmd match {
        case BenchToBench(bIndex1, bIndex2) => (pData.owner, args.length) match {
            case (p, 0) => checkNotAdditional(p, p.bench(bIndex1), p.bench(bIndex2))
            case (p, _) => attachElectrode(p, p.bench(bIndex2), args)
        }
        case BenchToActive(bIndex) => (pData.owner, args.length) match {
            case (p, 0) => checkNotAdditional(p, p.bench(bIndex), p.active)
            case (p, _) => attachElectrode(p, p.active, args) 
        }
        case ActiveToBench(bIndex) => (pData.owner, args.length) match {
            case (p, 0) => checkNotAdditional(p, p.active, p.bench(bIndex))
            case (p, _) => attachElectrode(p, p.bench(bIndex), args)
        }
        case _ => ()
    }

}