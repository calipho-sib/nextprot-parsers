package org.nextprot.parser.bed.utils

trait JSNode {
    def getId(): String
    def getTemplate(): String
}


class JSBioObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "b" + iteration + title.hashCode().abs
	}

  def getTemplate()  : String = {
    return ("var " + getId + " = createBioObject('" + title + "');");
  }
}

class JSTermObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "t" + iteration + title.hashCode().abs
	}

  def getTemplate()  : String = {
    return ("var " + getId + " = createTerm('" + title + "');");
  }
}

class JSDescriptionObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "d" + iteration + title.hashCode().abs
	}

  def getTemplate()  : String = {
    return ("var " + getId + " = createDescription('" + title + "');");
  }
}

class JSImpactObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "i" + iteration + title.hashCode().abs
	}

  def getTemplate()  : String = {
    return ("var " + getId + " = createModificationType('" + title + "');");
  }
}

class JSSubjectComparedObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "e" + iteration + title.hashCode().abs
	}

  def getTemplate()  : String = {
    return ("var " + getId + " = createSubjectCompared('" + title + "');");
  }
}

class JSVariantObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "n" + iteration + title.hashCode().abs
	}

  def getTemplate()  : String = {
    return ("var " + getId + " = createVariant('" + title + "');");
  }
}

class JSNoteObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "n" + iteration + title.hashCode().abs
	}

  def getTemplate()  : String = {
    return ("var " + getId + " = createNote('" + title + "');");
  }
}

class JSAnnotationObject(iteration: Integer, title: String) extends JSNode {

	def getId : String = {
	  "n" + iteration + title.hashCode().abs;
	}

  def getTemplate : String = {
    return ("var " + getId + " = createAnnotation('" + title + "');");
  }
}

class JSLinkObject (iteration: Integer, n1 : JSNode, n2 : JSNode, label: String) extends JSNode{

	def getId : String = {
	  "l" + iteration + n1.getId + n2.getId;
	}

  def getTemplate () : String = {
      return ("var " + getId + " = createLink(" + n1.getId + ", " + n2.getId + ",'" + label + "');")
  }
}
