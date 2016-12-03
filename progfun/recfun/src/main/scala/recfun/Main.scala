package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
    def pascal(c: Int, r: Int): Int = {
      if (c == 0) 1
      else if (c == r) 1
      else pascal(c - 1, r - 1) + pascal(c, r - 1)
    }
  
  /**
   * Exercise 2
   */
    def balance(chars: List[Char]): Boolean = {

      def loop(chars: List[Char], leftParens: Int): Boolean = {
        if (chars.isEmpty) return leftParens == 0

        val head :: tail = chars
        if (head == ')' && (leftParens == 0)) false
        else if (head == ')' && (leftParens > 0)) loop(tail, leftParens - 1)
        else if (head == '(') loop(tail, leftParens + 1)
        else loop(tail, leftParens)
      }

      loop(chars, 0)
    }

  /**
   * Exercise 3
   */
    def countChange(money: Int, coins: List[Int]): Int = {

      if (coins.isEmpty) {
        0
      } else if (money == 0) {
        1
      } else if (money < 0) {
        0
      } else {
        val head :: tail = coins.sorted
        countChange(money - head, coins) + countChange(money, tail)
      }
    }
  }
