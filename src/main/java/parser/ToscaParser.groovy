package parser

import groovy.io.FileType

import org.yaml.snakeyaml.Yaml

import utils.Logger

import com.esotericsoftware.yamlbeans.YamlConfig
import com.esotericsoftware.yamlbeans.YamlException
import com.esotericsoftware.yamlbeans.YamlReader

class ToscaParser {

	static boolean validate_yaml(final String text) {
		// Check YAML
		try {
			new Yaml().load(text)
		}
		catch(Exception e) {
			Logger.error "Not a valid YAML source"
			Logger.error e.toString()
			return false
		}

		return true
	}

	static boolean validate_tosca_yaml(final String text) {

		if (!validate_yaml(text)) {
			return false
		}

		String rule = 'Source should not have duplicate keys'
		YamlConfig yamlConfig = new YamlConfig()
		yamlConfig.setAllowDuplicates(false)
		YamlReader yamlReader = new YamlReader(text, yamlConfig)
		def model
		try {
			model = yamlReader.read()
		} catch (YamlException e) {
			Logger.error(rule)
			Logger.error("$e")
			return false
		}

		rule = "Source must be structured as a Yaml Map"
		if (!(model instanceof Map) ) {
			Logger.error(rule)
			return false
		}

		def ok = true

		ok &= validate_language_definition(text)
		ok &= validate_service_template(model)
		ok &= validate_topology_template_keywords(model)
		ok &= validate_node_templates_keywords(model)
		
		return ok
	}

	static boolean validate_language_definition(String text) {
		def rule = "Source must begin with 'tosca_definitions_version'"
		if (!text.startsWith('tosca_definitions_version:')&&!text.startsWith('{tosca_definitions_version:')) {
			Logger.error(rule)
			return false
		}
		return true
	}

	static boolean validate_service_template(Map model) {
		def valid_keywords = [
			'tosca_definitions_version', 
			'metadata',
			'template_name', // should be inside metadata map but seems accepted at root level
			'template_version', // should be inside metadata map but seems accepted at root level
			'template_author', // should be inside metadata map but seems accepted at root level
			'description', 
			'imports', 
			'dsl_definitions', 
			'repositories',
			'artifact_types',
			'data_types',
			'capability_types',
			'interface_types',
			'relationship_types',
			'node_types',
			'group_types',
			'policy_types',
			'topology_template'
			]
		def rule = "Valid keywords for a service template are $valid_keywords"
		def ok = true
		model.each {k,v ->
			if (!valid_keywords.contains(k)) {
				Logger.error(rule)
				Logger.error("'$k' is not a valid keyword for a service template")
				ok = false
			}
		}
		return ok
	}
	
	static boolean validate_topology_template_keywords(Map model) {
		def valid_topology_template_keywords = [
			'description',
			'node_templates', 
			'inputs', 
			'outputs', 
			'relationship_templates', 
			'substitution_mappings',
			'worflows'
			]
		def rule = "Valid keywords in topology_template block are $valid_topology_template_keywords"
		def ok = true
		model['topology_template']?.each {k,v ->
			if (!valid_topology_template_keywords.contains(k)) {
				Logger.error(rule)
				Logger.error("'$k' is not a valid keyword in topology_template block")
				ok = false
			}
		}
		return ok
	}
	
	static boolean validate_node_templates_keywords(Map model) {
		def valid_node_templates_keywords = [
			'type',
			'description',
			'directives',
			'properties',
			'attributes',
			'requirements',
			'capabilities',
			'interfaces',
			'artifacts',
			'node_filter',
			'copy'
		]
		def rule = "Valid keywords for a node template are $valid_node_templates_keywords"
		def ok = true
		model.'topology_template'?.'node_templates'?.each {ntmpl_name, ntmpl ->
			if (!validate_block(ntmpl, valid_node_templates_keywords)) {
				Logger.error(rule)
				Logger.error("'$ntmpl_name' is not a valid block in node_templates block")
				ok = false
			}
			//ok &= validate_node_template_properties(ntmpl_name, ntmpl)
		}
		return ok
	}
	
	static boolean validate_node_type_properties(String ntmpl_name, ntmpl) {
		if (!(ntmpl instanceof Map)) {
			Logger.error "Node template '$ntmpl_name' is not a Map"
			return false
		}
		def p = ntmpl.'properties'
		if (p == null) {
			return true
		}
		if (!(p instanceof Map)) {
			Logger.error "Node template '$ntmpl_name' properties is not a Map"
			return false
		}
		def valid_properties_keywords = [
			'type', 'description', 'required', 'default',
			'status', 'constraints', 'entry_schema']
		def rule = "Valid keywords from properties are '$valid_properties_keywords'"
		def ok = true
		p.each {k,v ->
			if (!valid_properties_keywords.contains(k)) {
				Logger.error("'$k' is not a valid keyword")
				ok = false
			}
		}
		return ok
	}

	static boolean validate_block(block, List<String> valid_keywords) {
		if (!(block instanceof Map)) {
			Logger.error('Block is not a Map')
			return false
		}

		def ok = true
		Map b = block
		b.each { k,v ->
			if (!valid_keywords.contains(k)) {
				Logger.error("'$k' is not a valid keyword")
				ok = false
			}
		}

		return ok
	}	

}

