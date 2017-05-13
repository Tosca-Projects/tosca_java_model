package parser;

import static org.junit.Assert.*;

import org.junit.Test;

import builder.ToscaBuilder;

class TestToscaSyntax extends GroovyTestCase {

	@Test
	public void test_version_1_1() {
		assert shouldFail { ToscaSyntax.getServiceTemplateSyntax("1.0") } == "only version 1.1 is supported at this time"
		def s = ToscaSyntax.getServiceTemplateSyntax("1.1")
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
		assert s.check(ToscaBuilder.complex_service_template()).OK
		assert s.check(ToscaBuilder.simple_imperative_workflow()).OK
	}

}
