package fhir.searchParam

import org.hl7.fhir.instance.model.Resource
import org.hl7.fhir.instance.model.Conformance.SearchParamType
import org.w3c.dom.Node

import com.mongodb.BasicDBObject


// FHIR ballot spec doens't fully explain how Text is different from Search...
// for now we'll treat them the same.
public class TextSearchParamHandler extends StringSearchParamHandler {
	
	
}

public class StringSearchParamHandler extends SearchParamHandler {

	@Override
	protected String paramXpath() {
		return "//$xpath//@value";
	}

	@Override
	public void processMatchingXpaths(List<Node> nodes, List<SearchParamValue> index) {
		setMissing(nodes.size() == 0, index);
		String parts = nodes.collect {it.nodeValue}.join(" ")
		index.add(value(parts))
	}

	@Override
	BasicDBObject searchClause(Map searchedFor){
		
		def val = stripQuotes(searchedFor)
		
		if (searchedFor.modifier == null){
			return match(
				k: fieldName,
				v: [
					$regex: '^'+val,
					$options: 'i'
				]
			)
		}
		
		if (searchedFor.modifier == "exact"){
			return match(
				k: fieldName,
				v: [
					$regex: '^'+val+'$'
				]
			)
		}
		
		if (searchedFor.modifier == "partial"){
			return match(
				k: fieldName,
				v: [
					$regex: val+'$',
					$options: 'i'
				]
			)
		}
		throw new RuntimeException("Unknown modifier: " + searchedFor)
	}
}