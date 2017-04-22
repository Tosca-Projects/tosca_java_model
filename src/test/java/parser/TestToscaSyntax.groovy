package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import builder.ToscaBuilder;

class TestToscaSyntax {

	@Test
	public void test() {
		def s = new ToscaSyntax("service_template")
		s.root_entry.a("map").with {
			string_entry("tosca_definitions_version").mandatory()
			map_entry("metadata").with {
				string_entry "template_name"
				string_entry "template_version"
				string_entry "template_author"
			}
			string_entry "template_name"
			string_entry "template_version"
			string_entry "template_author"
			string_entry "description"
			map_entry("dsl_definitions").with { 
				any_entry().with {
					any_entry()
				}
			}
			map_entry("repositories").with {
				any_entry().with {
					string_entry("description")
					string_entry("url")
					map_entry("credential").with {
						string_entry("protocol")
						string_entry("token_type").mandatory()
						string_entry("token").mandatory()
						map_entry("keys")
						string_entry("user")
					}
				}
			}
			list_entry("imports").with {
				any_map_entry().with {
					string_entry("file").mandatory()
					string_entry("repository")
					string_entry("namespace_uri")
					string_entry("namespace_prefix")
					or_a("string")
				}
			}
			map_entry("artifact_types").with {
				any_map_entry().with {
					string_entry "derived_from"
					string_entry "version"
					map_entry("metadata").with {
						string_entry "template_name"
						string_entry "template_version"
						string_entry "template_author"
					}
					string_entry "description"
					string_entry "mime_type"
					list_entry("file_ext")
					properties_entry()
				}
			}
			map_entry("topology_template").with {
				string_entry "description"
				map_entry("inputs").with {  
					any_map_entry().with {
						string_entry "type"
						string_entry "description"
						boolean_entry "required"
						entry "default"
						string_entry "status"
						list_entry("constraints").with {  
							// TODO	
						}
					}
				}
				map_entry("node_templates").with {
					any_map_entry().with {
						string_entry("type").mandatory()
						string_entry "description"
						map_entry("metadata").with {
							string_entry "template_name"
							string_entry "template_version"
							string_entry "template_author"
						}
						list_entry "directives"
						properties_entry()
						map_entry("capabilities").with {  
							any_map_entry().with {
								map_entry("properties").with { any_entry() }
								map_entry("attributes").with { any_entry() }
							}
						}
					}
				}
			}
		}
		assert s.check(ToscaBuilder.simple_service_template()).OK
		assert s.check(ToscaBuilder.simple_service_template2()).OK
		assert s.check(ToscaBuilder.wrong_service_template()).OK == false
		assert s.check(ToscaBuilder.wrong_service_template2()).OK == false
		assert s.check(ToscaBuilder.simple_topology_template()).OK
		assert s.check(ToscaBuilder.simple_dsl_definitions()).OK
		assert s.check(ToscaBuilder.simple_repositories()).OK
		assert s.check(ToscaBuilder.simple_imports()).OK
		assert s.check(ToscaBuilder.simple_imports2()).OK
		assert s.check(ToscaBuilder.simple_inputs()).OK
	}
}
