package idealized.Scala

/**
  * Created by phil on 8/10/16.
  */
abstract class Nat {
  def isZero: Boolean
  def predecessor: Nat
  def successor: Nat
  def + (that: Nat): Nat
  def - (that: Nat): Nat
}

object Zero extends Nat {
  override def isZero: Boolean = true

  override def predecessor: Nat = throw new Exception

  override def successor: Nat = new Succ(Zero)

  override def +(that: Nat): Nat = that

  override def -(that: Nat): Nat = if (that.isZero) Zero else throw new Exception
}

class Succ(n: Nat) extends Nat {
  override def isZero: Boolean = false

  override def predecessor: Nat = n

  override def successor: Nat = new Succ(this)

  override def +(that: Nat): Nat = new Succ(n + that)

  override def -(that: Nat): Nat = {
    if (that.isZero) this
    else this.predecessor - that.predecessor
  }
}