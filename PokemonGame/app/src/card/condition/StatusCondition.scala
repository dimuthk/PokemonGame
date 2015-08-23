package src.card.condition

object StatusCondition extends Enumeration {

  type StatusCondition = Value

  val PARALYZED = Value("Paralyzed")
  val CONFUSED = Value("Confused")
  val ASLEEP = Value("Asleep")
}
