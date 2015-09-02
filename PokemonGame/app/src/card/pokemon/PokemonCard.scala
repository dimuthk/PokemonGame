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
	private val _energyType : EnergyType.Value,
	val weakness : Option[EnergyType.Value] = None,
  val resistance : Option[EnergyType.Value] = None,
  val retreatCost : Int) extends Card(displayName, imgName, deck) {

  val pass : Move = new Pass()

  private var _currHp : Int = maxHp

  def currHp = _currHp

  def energyType = _energyType

  var energyCards : Seq[EnergyCard] = List()

  var preEvolution : Option[PokemonCard] = None

  var poisonStatus : Option[PoisonStatus.Value] = None

  var statusCondition : Option[StatusCondition.Value] = None

  var generalCondition : Option[String] = None

  override def getIdentifier() = identifier

	override def toJsonImpl() = customMoveJson(None)

  override def customMoveJson(customMoveArray : Option[JsArray]) : JsObject = super.toJsonImpl() ++ Json.obj(
    Identifier.DISPLAY_NAME.toString -> displayName,
    Identifier.MAX_HP.toString -> maxHp,
    Identifier.CURR_HP.toString -> currHp,
    Identifier.IMG_NAME.toString -> imgName,
    Identifier.ENERGY_CARDS.toString -> cardListToJsArray(energyCards),
    Identifier.ENERGY_TYPE.toString -> energyType,
    Identifier.MOVES.toString -> (customMoveArray match {
      case Some(moveArray) => moveArray
      case None => optionMoveListToJsArray(List(firstMove, secondMove, Some(pass)))
    }),
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
  //var stuck : Boolean = false

  /**
   * 50% chance the move will work.
   */
  var smokescreen : Boolean = false

  /**
   * Damage, but nothing else, is prevented on this card the following turn.
   */
   var withdrawn : Boolean = false

   /**
    * Damage, status conditions, and forced moves are prevented on this card
    * the following turn.
    */
   var agility : Boolean = false

   /**
    * This pokemon can't move this turn.
    */
   var acid : Boolean = false

   /**
    * Tracker for the last effect made on this pokemon. Used for mirror move
    */
   var lastAttack : Int = -1

   /**
    * Damage done to this pokemon this turn is reduced by 10.
    */
   var pounced : Boolean = false

   /**
    * Damage done to this pokemon this turn is reduced by 30.
    */
   var harden : Boolean = false




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
    _currHp = maxHp
    poisonStatus = None
    statusCondition = None
    var res : Seq[Card] = energyCards
    energyCards = Nil
    res = res ++ preEvolutions(this)
    preEvolution = None
    lastAttack = -1
    smokescreen = false
    harden = false
    acid = false
    agility = false
    withdrawn = false
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

  def updateCardOnTurnSwap(owner : Player, opp : Player, isActive : Boolean) : Unit = { isActive match {
    case true => {
      if (owner.isTurn) {
        withdrawn = false
        pounced = false
        harden = false
        agility = false
      } else {
        smokescreen = false
        lastAttack = -1
        acid = false
      }
    }
    case false => {
      lastAttack = -1
      statusCondition = None
      pounced = false
      harden = false
      poisonStatus = None
      withdrawn = false
      agility = false
      smokescreen = false
    }
    }
    var cond =""
    if (withdrawn) {
      cond = cond + "Withdrawn"
    }
    if (smokescreen) {
      cond = cond + "Smokescreen"
    }
    if (agility) {
      cond = cond + "Agility"
    }
    if (pounced) {
      cond = cond + "Pounced"
    }
    if (acid) {
      cond = cond + "Acid"
    }
    if (harden ) {
      cond = cond + "Harden"
    }
    generalCondition = Some(cond)
  }

  def calculateDmg(attacker : PokemonCard, dmg : Int) : Int = {
    if (withdrawn || agility) {
      return 0
    }
    var modifiedDmg = dmg
    
    // Resistance / weakness modifier
    if (weakness.exists { eType => eType == attacker.energyType }) {
      modifiedDmg *= 2
    }
    
    if (resistance.exists { eType => eType == attacker.energyType }) {
      modifiedDmg -= 30
    }

    if (harden && modifiedDmg <= 30) {
      modifiedDmg = 0
    }

    return math.max(modifiedDmg, 0)
  }

	def takeDamage(attacker : Option[PokemonCard], amount : Int) : Unit = {
    var dmg = amount
    if (withdrawn || agility) {
      return
    }
    if (pounced && dmg > 0) {
      dmg = dmg - 10
      pounced = false
    }
    lastAttack = dmg
    _currHp = math.max(_currHp - dmg, 0)
  }

  def heal(amount : Int) : Unit = (amount <= 0) match {
    case true => ()
    case false => _currHp = math.min(_currHp + amount, maxHp)
  }

  def isEvolutionOf(pc : PokemonCard) : Boolean

}
