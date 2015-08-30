package src.card.pokemon

import play.api.libs.json._
import src.card.Placeholder
import src.card.Card
import src.card.Deck
import src.player.Player
import src.card.condition.GeneralCondition
import src.card.condition.PoisonStatus
import src.card.condition.StatusCondition
import src.card.energy.EnergyCard
import src.card.energy.EnergyType
import src.json.Identifier
import src.move.Move
import src.move.Pass

import play.api.Logger

abstract class PokemonCard(
	displayName : String,
	imgName : String,
  deck : Deck.Value,
  val identifier : Identifier.Value,
	val id : Int,
	val maxHp : Int,
	val firstMove: Option[Move] = None,
  val secondMove: Option[Move] = None,
	val energyType : EnergyType.Value,
	val weakness : Option[EnergyType.Value] = None,
  val resistance : Option[EnergyType.Value] = None,
  val retreatCost : Int) extends Card(displayName, imgName, deck) {

  val pass : Move = new Pass()

  private var _currHp : Int = maxHp

  def currHp = _currHp

  var energyCards : Seq[EnergyCard] = List()

  var preEvolution : Option[PokemonCard] = None

  var poisonStatus : Option[PoisonStatus.Value] = None

  var statusCondition : Option[StatusCondition.Value] = None

  var generalCondition : Option[String] = None

  override def getIdentifier() = identifier

	override def toJsonImpl() = super.toJsonImpl() ++ Json.obj(
		Identifier.DISPLAY_NAME.toString -> displayName,
		Identifier.MAX_HP.toString -> maxHp,
		Identifier.CURR_HP.toString -> currHp,
		Identifier.IMG_NAME.toString -> imgName,
    Identifier.ENERGY_CARDS.toString -> cardListToJsArray(energyCards),
    Identifier.ENERGY_TYPE.toString -> energyType,
    Identifier.MOVES.toString -> optionMoveListToJsArray(List(firstMove, secondMove, Some(pass))),
    Identifier.POISON_STATUS.toString -> (poisonStatus match {
      case Some(p : PoisonStatus.Value) => p.toString
      case None => "None"
    }),
    Identifier.STATUS_CONDITION.toString -> (statusCondition match {
      case Some(s : StatusCondition.Value) => s.toString
      case None => "None"
    }),
    Identifier.GENERAL_CONDITION.toString -> (generalCondition match {
      case Some(s : String) => s
      case None => Placeholder.toJson()
    }))

  def existingMoves : Seq[Move] = List(firstMove, secondMove).flatten

  def getMove(i : Int) : Option[Move] = i match {
    case 1 => firstMove
    case 2 => secondMove
    case _ => None
  }

  /**
   * Flag marking that this card cannot be moved from its current spot.
   */
  def stuck : Boolean = false

  def ownsMove(m : Move) : Boolean = existingMoves.filter(_ == m).length > 0

  def getTotalEnergy(oEType : Option[EnergyType.Value] = None) : Int = oEType match {
    case Some(eType) => energyCards.filter(_.eType == eType).length
    case None => energyCards.length
  }

  def evolveOver(pc : PokemonCard) {
    energyCards = pc.energyCards
    pc.energyCards = Nil
    preEvolution = Some(pc)
  }

  def pickUp() : Seq[Card] = {
    if (stuck) {
      throw new Exception("Tried to pick up card when it was stuck")
    }
    _currHp = maxHp
    poisonStatus = None
    statusCondition = None
    var res : Seq[Card] = energyCards
    energyCards = Nil
    res = res ++ preEvolutions(this)
    preEvolution = None
    return List(this) ++ res
  }

  def preEvolutions(pc : PokemonCard) : Seq[PokemonCard] = pc.preEvolution match {
    case None => Nil
    case Some(preEvolution) => List(preEvolution) ++ preEvolutions(preEvolution)
  }

  def discardEnergy(eType : EnergyType.Value, cnt : Int = 1) : Seq[EnergyCard] = {
    val matchingEnergy : Seq[EnergyCard] = (() => eType match {
      case EnergyType.COLORLESS => energyCards.slice(0, cnt)
      case other => energyCards.filter(_.eType == other).slice(0, cnt)
    })()
    energyCards = energyCards diff matchingEnergy
    return matchingEnergy
  }

  /**
   * Updates the list of moves for this pokemon. This method will be triggered after any operation
   * made by either player.
   * @param turnSwapped Whether or not the turn was swapped during this operation.
   * @param whether or not this card is the active card. false implies this is a bench card.
   */
  def updateMoves(owner : Player, opp : Player, turnSwapped : Boolean, isActive : Boolean) : Unit
    = (existingMoves ++ List(pass)).foreach(_.update(owner, opp, this, turnSwapped, isActive))

  def updateCardOnTurnSwap(owner : Player, opp : Player, isActive : Boolean) : Unit = isActive match {
    case true => ()
    case false => {
      statusCondition = None
      poisonStatus = None
      generalCondition = None
    }
  }

  def calculateDmg(attacker : PokemonCard, dmg : Int) : Int = {
    var modifiedDmg = dmg
    
    // Resistance / weakness modifier
    if (weakness.exists { eType => eType == attacker.energyType }) {
      modifiedDmg *= 2
    }
    
    if (resistance.exists { eType => eType == attacker.energyType }) {
      modifiedDmg -= 30
    }

    return math.max(modifiedDmg, 0)
  }

	def takeDamage(amount : Int) : Unit = (amount <= 0) match {
    case true => ()
    case false => _currHp = math.max(_currHp - amount, 0)
  }

  def heal(amount : Int) : Unit = (amount <= 0) match {
    case true => ()
    case false => _currHp = math.min(_currHp + amount, maxHp)
  }

  def isEvolutionOf(pc : PokemonCard) : Boolean

}
