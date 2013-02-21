package yairs.util

import yairs.model.QueryTreeNode
import org.eintr.loglady.Logging
import collection.mutable
import io.Source
import collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: Hector, Zhengzhong Liu
 * Date: 2/20/13
 * Time: 11:19 AM
 */
object PrefixBooleanQueryParser extends QueryParser with Logging {
  private val stopWordDict = Source.fromFile("data/stoplist.txt").getLines().toSet

  def isStop(word:String) = stopWordDict.contains(word.trim)

  def parseNode(str: String): QueryTreeNode = {
    if (str.startsWith("#OR")) {
      new QueryTreeNode("or", stripOuterBrackets(str.stripPrefix("#OR")))
    } else if (str.startsWith("#AND")) {
      new QueryTreeNode("and", stripOuterBrackets(str.stripPrefix("#AND")))
    } else if (str.startsWith("#NEAR")) {
      new QueryTreeNode("near", stripOuterBrackets(str.stripPrefix("#NEAR")))
    } else {
      new QueryTreeNode("", stripOuterBrackets(str))
    }
  }


  def split(subQuery: String): List[String] = {
    val strBuffer = new StringBuilder
    val bracketStack = new mutable.Stack[Char]

    var subNodeStrBuffer = ListBuffer.empty[String]

    subQuery.foreach(char =>{
      if (char == '(') {
        bracketStack.push(char)
      }
      if (char == ')') {
        bracketStack.pop()
      }

      if (char == ' ' && bracketStack.isEmpty && !isOperator(strBuffer.toString().trim)) {
        subNodeStrBuffer += strBuffer.toString()
        strBuffer.clear()
      }
      else{
        strBuffer.append(char)
      }
    })
    subNodeStrBuffer += strBuffer.toString()

//ListBuffer is more efficient
//    val subNodeStrs = subQuery.foldLeft(List[String]())((strs, char) => {
//      if (char == '(') {
//        bracketStack.push(char)
//      }
//      if (char == ')') {
//        bracketStack.pop()
//      }
//
//      if (char == ' ' && bracketStack.isEmpty && !isOperator(strBuffer.toString().trim)) {
//        val newStrs = strs ::: List(strBuffer.toString())
//        strBuffer.clear()
//        newStrs
//      }
//      else{
//        strBuffer.append(char)
//        strs
//      }
//    }) ::: List(strBuffer.toString())

    subNodeStrBuffer.toList
  }

  def isOperator(str: String) = (str == "#AND" || str == "#OR" || str == "#NEAR")

  def stripOuterBrackets(str: String):String = {
    val trimmed = str.trim
    if (trimmed.startsWith("(")&&trimmed.endsWith(")")) {
      trimmed.stripPrefix("(").stripSuffix(")")
    }else{
      trimmed
    }
  }
}
