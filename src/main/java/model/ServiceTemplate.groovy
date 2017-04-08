package model

class ServiceTemplate {

	Map model
	// TODO supprimer et remplacer par des getters
	List<DataType> data_types = []
	List<CapabilityType> capability_types = []
	List<InterfaceType> interface_types = []
	List<RelationshipType> relationship_types = []
	List<NodeType> node_types = []
	List<GroupType> group_types = []
	List<PolicyType> policy_types = []
	TopologyTemplate topology_template

	ServiceTemplate(model) {
		if (!(model instanceof Map)) {
			throw new Exception("a service template definition should be a map")
		}
		this.model = model
		model.'artifact_types'.each { artifact_types << new ArtifactType(it) }
		model.'data_types'.each { data_types << new DataType(it) }
		model.'capability_types'.each { capability_types << new CapabilityType(it) }
		model.'interface_types'.each { interface_types << new InterfaceType(it) }
		model.'relationship_types'.each { relationship_types << new RelationshipType(it) }
		model.'node_types'.each { node_types << new NodeType(it) }
		model.'group_types'.each { group_types << new GroupType(it) }
		model.'policy_types'.each { policy_types << new PolicyType(it) }
		if (model.'topology_template') {
			topology_template = new TopologyTemplate(model.'topology_template')
		}
	}
	
	public String getTosca_definitions_version() {
		return model.'tosca_definitions_version'
	}
	
	public Map<String,String> getMetadata() {
		if (model.'metadata') {
			ToscaModel.checkIsMap(model, 'metadata')
			return model.'metadata'
		}
		return [:]
	}
	
	public String getTemplate_name() {
		if (model.'template_name') {
			return model.'template_name'
		}
		return getMetadata().'template_name'
	}
	
	public String getTemplate_version() {
		if (model.'template_version') {
			return model.'template_version'
		}
		return getMetadata().'template_version'
	}

	public String getTemplate_author() {
		if (model.'template_author') {
			return model.'template_author'
		}
		return getMetadata().'template_author'
	}
	
	public String getDescription() {
		return model.'description'
	}
	
	public Map getDsl_definitions() {
		if (model.'dsl_definitions') {
			ToscaModel.checkIsMap(model, 'dsl_definitions')
			return model.'dsl_definitions'
		}
		return [:]
	}
	
	public List<Repository> getRepositories() {
		ToscaModel.checkIsMap(model, 'repositories')
		def result = []
		this.model.'repositories'.each { repo_name, repo_model -> 
			result << new Repository(repo_name, repo_model) 
		}
		return result
	}
	
	public List<Import> getImports() {
		ToscaModel.checkIsList(model, 'imports')
		def result = []
		this.model.'imports'.each { import_model -> 
			result << new Import(import_model) 
		}
		return result
	}
	
	public List<ArtifactType> getArtifact_types() {
		ToscaModel.checkIsMap(model, 'artifact_types')
		def result = []
		this.model.'artifact_types'.each { at_name, model ->
			result << new ArtifactType(at_name, model)
		}
		return result
	}
	
	public List<DataType> getData_types() {
		ToscaModel.checkIsMap(model, 'data_types')
		def result = []
		this.model.'data_types'.each { dt_name, model ->
			result << new DataType(dt_name, model)
		}
		return result
	}
	
	public List<CapabilityType> getCapability_types() {
		ToscaModel.checkIsMap(model, 'capability_types')
		def result = []
		this.model.'capability_types'.each { ct_name, model ->
			result << new CapabilityType(ct_name, model)
		}
		return result
	}
	
	public List<InterfaceType> getInterface_types() {
		ToscaModel.checkIsMap(model, 'interface_types')
		def result = []
		this.model.'capabiinterface_types'.each { it_name, model ->
			result << new InterfaceType(it_name, model)
		}
		return result
	}
	
}
