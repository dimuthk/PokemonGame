package src.board

import actors.PlayerActor

import scala.util.Random

import src.card.Card
import src.card.energy._
import src.card.pokemon._
import src.card.pokemon.base_set._
import src.card.pokemon.jungle._
import src.move.Move
import src.move.PokemonPower
import src.move.Status
import src.player.Player

import play.api.Logger
import play.api.libs.json._

import akka.actor.ActorRef
import akka.actor._
import play.api.libs.json._

case class BoardCommand(cmd : String)

case class StateCommand(state : String)

case class AddChild(actorRef : ActorRef)

object Board {

  val eventBus = new StateEventBus()

  var c1 : Option[Correspondent] = None
  var c2 : Option[Correspondent] = None

  def canAccept : Boolean = c1.isEmpty || c2.isEmpty

  def forkPlayerActor(out : ActorRef) : Props = {
    if (c1.isEmpty) {
      c1 = Some(new Correspondent(eventBus, 1))
      return PlayerActor.props(out, c1.get)
    } else if (c2.isEmpty) {
      c2 = Some(new Correspondent(eventBus, 2))
      return PlayerActor.props(out, c2.get)
    }
    throw new Exception("No correspondents available")
  }

  def onTerminateSocket(id : Int) = {
    if (id == 1) {
      c1 = None
    } else if (id == 2) {
      c2 = None
    }
  }

  def getOpponent(p : Player) : Player = return if (p == c1.get.p) c2.get.p else c1.get.p

  class Correspondent(val eventBus : StateEventBus, val id : Int) {

    val p : Player = new Player()

    /**
     * Signals that the deck has been established for this correspondent.
     * Gameplay will not begin until this has happened
     */
    var ready = false

    def populateMachop : Unit = {
      if (id == 1) {
        p.deck = List.fill(20)(new Squirtle()) ++ List.fill(20)(new WaterEnergy())
        p.deck = Random.shuffle(p.deck)
        p.active = Some(new Venusaur())
        p.active.get.isFaceUp = true
        p.active.get.isClickable = true
        p.active.get.isUsable = true
        p.isTurn = true
      } else {
        p.deck = List.fill(20)(new Caterpie()) ++ List.fill(20)(new Metapod()) ++ List.fill(20)(new GrassEnergy())
        p.deck = Random.shuffle(p.deck)
        p.bench(2) = Some(new Charizard())
        p.bench(2).get.isFaceUp = true
        p.bench(2).get.isClickable = true
        p.bench(2).get.isUsable = true
        p.isTurn = false
      }

      distributeInitialCards()
      ready = true
      if (bothCorrespondentsReady) {
        broadcastState()
      }
    }

    def rebroadcastState() {
      broadcastState()
    }

    def attackUsing(pc : PokemonCard, moveNum : Int) {
      val move = if (moveNum == 1) pc.firstMove.get else pc.secondMove.get
      if (move.isActivatable) {
        move.status match {
          case Status.ACTIVATABLE => move.status = Status.ACTIVATED
          case Status.ACTIVATED => move.status = Status.ACTIVATABLE
          case _ => ()
        }
      } else {
        move.perform(p, getOpponent(p))
      }
      move match {
        case power : PokemonPower => ()
        case _ => flipTurn()
      }
      updateMoveStatuses()
      broadcastState()
    }

    def attackFromActive(moveNum : Int) {
      Logger.debug("attack " + moveNum)
      attackUsing(p.active.get, moveNum)
    }

    def attackFromBench(moveNum : Int, benchIndex : Int) {
      attackUsing(p.bench(benchIndex).get, moveNum)
    }

    def handleMove(
      move : (Map[String, Int]) => Unit,
      itemMap : Map[String, Int],
      moveName : String) {
        if (!allowMoveExceptions(moveName, itemMap)) {
          move(itemMap)
        }
        updateMoveStatuses()
        broadcastState()
    }

  def allowMoveExceptions(moveName : String, itemMap : Map[String, Int]) : Boolean = {
    if (p.active.isDefined) {
      for (m <- List(p.active.get.firstMove, p.active.get.secondMove)) {
        if (m.isDefined && m.get.status == Status.ACTIVATED) {
          // delegate move handle to pokemon with activated power
          m.get match {
            case power : PokemonPower => power.handleMove(p, getOpponent(p), moveName, itemMap)
            case _ => ()
          }
          return true
        }
      }
    }
    for (obc : Option[PokemonCard] <- p.bench) {
      if (obc.isDefined) {
        for (m <- List(obc.get.firstMove, obc.get.secondMove)) {
          if (m.isDefined && m.get.status == Status.ACTIVATED) {
            m.get match {
              case power : PokemonPower => power.handleMove(p, getOpponent(p), moveName, itemMap)
              case _ => ()
            }
            return true
          }
        }
      }
    }
    return false
  }

    def benchToBench(itemMap : Map[String, Int]) {
      val benchIndex1 = itemMap.getOrElse("drag", -1)
      val benchIndex2 = itemMap.getOrElse("drop", -1)
      val benchOne = p.bench(benchIndex1).get
      if (p.bench(benchIndex2).isDefined) {
        p.bench(benchIndex1) = Some(p.bench(benchIndex2).get)
        p.bench(benchIndex2) = Some(benchOne)
      } else {
        p.bench(benchIndex2) = Some(benchOne)
        p.bench(benchIndex1) = None
      }
    }

    def benchToActive(itemMap : Map[String, Int]) {
      val benchIndex = itemMap.getOrElse("drag", -1)
      if (p.active.isDefined) {
        swapActiveAndBench(benchIndex)
      } else {
        p.active = Some(p.bench(benchIndex).get)
        p.bench(benchIndex) = None
      }
    }

    def activeToBench(itemMap : Map[String, Int]) {
      val benchIndex = itemMap.getOrElse("drop", -1)
      if (p.bench(benchIndex).isDefined) {
        swapActiveAndBench(benchIndex)
      } else {
        val active = p.active.get
        if (active.getTotalEnergy() >= active.retreatCost) {
          active.energyCards = active.energyCards.dropRight(active.retreatCost)
          p.bench(benchIndex) = Some(active)
          p.active = None
        }
      }
    }

    def swapActiveAndBench(benchIndex : Int) {
      val active = p.active.get
      val benchCard = p.bench(benchIndex).get
      if (active.getTotalEnergy() >= active.retreatCost) {
        active.energyCards = active.energyCards.dropRight(active.retreatCost)
        p.active = Some(benchCard)
        p.bench(benchIndex) = Some(active)
      }
    }

    def handToActive(itemMap : Map[String, Int]) {
      val handIndex = itemMap.getOrElse("drag", -1)
      val card : Card = p.hand(handIndex)
      if (p.active.isEmpty) {
        card match {
          // Moving basic pokemon from hand to active slot.
          case pc : PokemonCard => {
            if (pc.evolutionStage == EvolutionStage.BASIC) {
              p.active = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      } else {
        val active = p.active.get
        card match {
          // Attaching energy card to active pokemon.
          case ec : EnergyCard => {
            active.energyCards = active.energyCards ++ List(ec)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
          // Evolving active pokemon.
          case pc : PokemonCard => {
            if (pc.isEvolutionOf(active)) {
              pc.evolveOver(active)
              p.active = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      }
    }

    def handToBench(itemMap : Map[String, Int]) {
      val handIndex = itemMap.getOrElse("drag", -1)
      val benchIndex = itemMap.getOrElse("drop", -1)
      Logger.debug("handToBench(" + handIndex + ", " + benchIndex + ")")
      val card : Card = p.hand(handIndex)
      if (p.bench(benchIndex).isEmpty) {
        card match {
          case pc : PokemonCard => {
            // Moving basic pokemon to empty bench slot.
            if (pc.evolutionStage == EvolutionStage.BASIC) {
              p.bench(benchIndex) = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      } else {
        val benchCard = p.bench(benchIndex).get
        card match {
          case ec : EnergyCard => {
            benchCard.energyCards = benchCard.energyCards ++ List(ec)
            p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
          }
          // Evolving this bench pokemon.
          case pc : PokemonCard => {
            if (pc.isEvolutionOf(benchCard)) {
              pc.evolveOver(benchCard)
              p.bench(benchIndex) = Some(pc)
              p.hand = p.hand.slice(0, handIndex) ++ p.hand.slice(handIndex + 1, p.hand.size)
            }
          }
          case _ => ()
        }
      }
    }

    /**
     * Takes deck and distributes original cards appropriately.
     */
    def distributeInitialCards() : Unit = {
      for (i <- 0 until 6) {
        p.prizes(i) = Some(p.deck(i))
      }
      p.deck = p.deck.slice(6, 60)
      p.hand = p.deck.slice(0, 7)
      Logger.debug("init hand: " + p.hand)
      p.deck = p.deck.slice(7, 54)
    }

    def terminateSocket() = onTerminateSocket(id)

  }

  def updateMoveStatuses() {
    for (p : Player <- List[Player](c1.get.p, c2.get.p)) {
      if (p.active.isDefined) {
        val active = p.active.get
        for (m <- List(p.active.get.firstMove, p.active.get.secondMove)) {
          if (m.isDefined) {
            if (m.get.isActivatable) {
              if (m.get.status != Status.ACTIVATED && active.statusCondition.isEmpty) {
                m.get.status = Status.ACTIVATABLE
              }
            } else {
              m.get match {
                // Passive powers
                case power : PokemonPower => power.status =
                  if (active.statusCondition.isEmpty) Status.PASSIVE else Status.DISABLED
                 // Active pokemon with non-activatable moves should be enabled if there's enough energy.
                case move : Move => move.status =
                  if (move.hasEnoughEnergy(p, active.energyCards)) Status.ENABLED else Status.DISABLED
              }
            
            }
          }
        }
      }

      for (pc : Option[PokemonCard] <- p.bench) {
        if (pc.isDefined) {
          for (m <- List[Option[Move]](pc.get.firstMove, pc.get.secondMove)) {
            if (m.isDefined) {
              if (m.get.isActivatable) {
                if (m.get.status != Status.ACTIVATED && pc.get.statusCondition.isEmpty) {
                  m.get.status = Status.ACTIVATABLE
                }
              } else {
                m.get match {
                  case power : PokemonPower => power.status = Status.PASSIVE
                  case move : Move => move.status = Status.DISABLED
                }
              }
            }
          }
        }
      }
    }
  }

  def flipTurn() : Unit = {
    c1.get.p.isTurn = !c1.get.p.isTurn
    c2.get.p.isTurn = !c2.get.p.isTurn
  }

  def bothCorrespondentsReady =
      !c1.isEmpty && !c2.isEmpty && c1.get.ready && c2.get.ready

  def broadcastState() {
    eventBus.publish(StateEvent(
      1, JsObject(Seq("player1" -> c1.get.p.toJson, "player2" -> c2.get.p.toJson))))
    eventBus.publish(StateEvent(
      2, JsObject(Seq("player1" -> c2.get.p.toJson, "player2" -> c1.get.p.toJson))))
    c1.get.p.notification = None
    c2.get.p.notification = None
  }

}
