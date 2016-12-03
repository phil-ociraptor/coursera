package patmat

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import patmat.Huffman._

@RunWith(classOf[JUnitRunner])
class HuffmanSuite extends FunSuite {
	trait TestTrees {
		val t1 = Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5)
		val t2 = Fork(Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5), Leaf('d',4), List('a','b','d'), 9)
	}


  test("weight of a larger tree") {
    new TestTrees {
      assert(weight(t1) === 5)
    }
  }


  test("chars of a larger tree") {
    new TestTrees {
      assert(chars(t2) === List('a','b','d'))
    }
  }


  test("string2chars(\"hello, world\")") {
    assert(string2Chars("hello, world") === List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'))
  }

  test("times") {
    assert(times(List('a', 'a', 'b', 'a', 'c', 'b')).toSet === List(('b', 2), ('a', 3), ('c', 1)).toSet)
  }

  test("makeOrderedLeafList for some frequency table") {
    assert(makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))) === List(Leaf('e',1), Leaf('t',2), Leaf('x',3)))
  }


  test("combine of some leaf list") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(combine(leaflist) === List(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4)))
  }

  test("until") {
    val leafList = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(until(singleton, combine)(leafList) === List(Fork(Fork(Leaf('e', 1), Leaf('t', 2), List('e', 't'), 3), Leaf('x', 4), List('e', 't', 'x'), 7)))
  }

  test("until (long leaf list)") {
    val leafList = List(Leaf('V',1), Leaf('S',1), Leaf('-',1), Leaf('H',1), Leaf('M',1), Leaf('R',1), Leaf('2',1), Leaf('B',1), Leaf('5',1), Leaf('4',1), Leaf('x',1), Leaf('v',2), Leaf('w',2), Leaf('.',3), Leaf('I',3), Leaf('k',3), Leaf('0',3), Leaf('b',4), Leaf('C',4), Leaf('L',5), Leaf('y',5), Leaf('f',7), Leaf('g',7), Leaf('h',8), Leaf(',',8), Leaf('p',10), Leaf('m',11), Leaf('u',12), Leaf('d',12), Leaf('c',14), Leaf('l',15), Leaf('n',19), Leaf('s',21), Leaf('i',24), Leaf('a',26), Leaf('t',26), Leaf('r',27), Leaf('o',33), Leaf('e',34), Leaf(' ',69))
//    println(until(singleton, combine)(leafList))
  }

  test("should decode hardcoded message") {
    assert(decodedSecret === List('h', 'u', 'f', 'f', 'm', 'a', 'n', 'e', 's', 't', 'c', 'o', 'o', 'l'))
  }

  test("should encode huffmanestcool") {
    assert(encode(frenchCode)("huffmanestcool".toList) == List(0,0,1,1,1,0,1,0,1,1,1,0,0,1,1,0,1,0,0,1,1,0,1,0,1,1,0,0,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,0,1,1,1,0,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1))
  }

  test("decode and encode a very short text should be identity") {
    new TestTrees {
      assert(decode(t1, encode(t1)("ab".toList)) === "ab".toList)
    }
  }

  test("decode and encode longer text should be identity") {
    new TestTrees {
      var message = "aaaaaaaaaabbbbbbbbbccccccccdddddddeeeeeefffff".toList
      val codeTree = createCodeTree(message)
      println(encode(codeTree)(message))
      assert(decode(codeTree, encode(codeTree)(message)) === message)
    }
  }

  test("decode and quickEncode a very short text should be identity") {
    new TestTrees {
      assert(decode(t1, quickEncode(t1)("ab".toList)) === "ab".toList)
    }
  }

  test("decode and quickEncode longer text should be identity") {
    new TestTrees {
      var message = "aaaaaaaaaabbbbbbbbbccccccccdddddddeeeeeefffff".toList
      val codeTree = createCodeTree(message)
      println(quickEncode(codeTree)(message))
      assert(decode(codeTree, quickEncode(codeTree)(message)) === message)
    }
  }
}
