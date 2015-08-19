package src.card.energy

import src.card.Card
import src.json.Identifier

import play.api.libs.json._

object EnergyType extends Enumeration {

  type EnergyType = Value

  val FIRE, WATER, GRASS, THUNDER, FIGHTING, PSYCHIC, COLORLESS = Value
}

abstract class EnergyCard(
    displayName : String,
    imgName : String,
    identifier : Identifier.Value,
    eType : EnergyType.Value,
    val energyCount : Int = 1) extends Card(displayName, imgName) {

  override def toJsonImpl() = Json.obj(
      Identifier.DISPLAY_NAME.toString -> displayName,
      Identifier.IMG_NAME.toString -> imgName,
      Identifier.ENERGY_TYPE.toString -> eType.toString)

  override def getIdentifier() = identifier

}

class FireEnergy extends EnergyCard(
    "Fire Energy",
    "Fire-Energy-Base-Set-98.jpg",
    Identifier.FIRE_ENERGY,
    EnergyType.FIRE)

class WaterEnergy extends EnergyCard(
    "Water Energy",
    "Water-Energy-Base-Set-102.jpg",
    Identifier.WATER_ENERGY,
    EnergyType.WATER)

class GrassEnergy extends EnergyCard(
    "Grass Energy",
    "Grass-Energy-Base-Set-99.jpg",
    Identifier.GRASS_ENERGY,
    EnergyType.GRASS)

class ThunderEnergy extends EnergyCard(
    "Thunder Energy",
    "Lightning-Energy-Base-Set-100.jpg",
    Identifier.THUNDER_ENERGY,
    EnergyType.THUNDER)

class FightingEnergy extends EnergyCard(
    "Fighting Energy", 
    "Fighting-Energy-Base-Set-97.jpg",
    Identifier.FIGHTING_ENERGY,
    EnergyType.FIGHTING)

class PsychicEnergy extends EnergyCard(
    "Psychic Energy",
    "Psychic-Energy-Base-Set-101.jpg",
    Identifier.PSYCHIC_ENERGY,
    EnergyType.PSYCHIC)

class DoubleColorlessEnergy extends EnergyCard(
    "Double Colorless Energy", 
    "Double-Colorless-Energy-Base-Set-96.jpg",
    Identifier.DOUBLE_COLORLESS_ENERGY,
    EnergyType.COLORLESS,
    2)
