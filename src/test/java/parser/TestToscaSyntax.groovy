package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import builder.ToscaBuilder;

class TestToscaSyntax {

	@Test
	public void test() {
		def s = new ToscaSyntax("service_template")
		s.root_entry.a("map").with {
			entry("tosca_definitions_version").mandatory()
			entry("metadata").a("map").with {
				entry "template_name"
				entry "template_version"
				entry "template_author"
			}
			entry "template_name"
			entry "template_version"
			entry "template_author"
			entry "description"
			entry("dsl_definitions").a("map").with { 
				any_entry().with {
					any_entry()
				}
			}
			entry("repositories").a("map").with {
				any_entry().with {
					entry "description"
					entry "url"
					entry("credential").a("map").with {
						entry "protocol"
						entry("token_type").mandatory()
						entry("token").mandatory()
						entry("keys").a("map")
						entry "user"
					}
				}
			}
			entry("imports").a("list").with {
				any_entry().a("map").with {
					entry("file").mandatory()
					entry "repository"
					entry "namespace_uri"
					entry "namespace_prefix"
					or_a("string")
				}
			}
			entry("topology_template").a("map").with {
				entry "description"
				entry("node_templates").a("map").with {
					any_entry().a("map").with {
						entry("type").mandatory()
						entry "description"
						entry("metadata").a("map").with {
							entry "template_name"
							entry "template_version"
							entry "template_author"
						}
						entry("directives").a("list")
						entry("properties").a("map")
						entry("capabilities").a("map").with {  
							any_entry().a("map").with {
								entry("properties").a("map").with { any_entry() }
								entry("attributes").a("map").with { any_entry() }
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
	}
}
