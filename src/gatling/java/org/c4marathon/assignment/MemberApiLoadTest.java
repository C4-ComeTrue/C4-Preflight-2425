package org.c4marathon.assignment;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class MemberApiLoadTest extends Simulation {
	private static final String PARALLEL_GET = "병렬 조회 시";
	private static final String BASIC_GET = "기본 조회 시";
	private static final String MEMBER_ID = "1369815";
	private static final String PARALLEL_API_ENDPOINT = "/api/v1/members/" + MEMBER_ID + "/parallel";
	private static final String BASIC_API_ENDPOINT = "/api/v1/members/" + MEMBER_ID;

	private final HttpProtocolBuilder httpProtocol = http
		.baseUrl("http://localhost:8080")
		.acceptHeader("application/json")
		.contentTypeHeader("application/json");

	private final ScenarioBuilder burstLoadScenarioParallel = scenario("Burst Load Parallel Test")
		.exec(http(PARALLEL_GET)
			.get(PARALLEL_API_ENDPOINT)
			.check(status().is(200))
		);

	private final ScenarioBuilder burstLoadScenario = scenario("Burst Load Test")
		.exec(http(BASIC_GET)
			.get(BASIC_API_ENDPOINT)
			.check(status().is(200))
		);

	private final ScenarioBuilder rampLoadScenarioParallel = scenario("Ramp Load Parallel Test")
		.exec(http(PARALLEL_GET)
			.get(PARALLEL_API_ENDPOINT)
			.check(status().is(200))
		);

	private final ScenarioBuilder rampLoadScenario = scenario("Ramp Load Test")
		.exec(http(BASIC_GET)
			.get(BASIC_API_ENDPOINT)
			.check(status().is(200))
		);

	{
		setUp(
			burstLoadScenarioParallel.injectOpen(
				nothingFor(Duration.ofSeconds(5)), // 5초간 정지
				atOnceUsers(100), // 한번에 100명의 유저가 동시에 요청
				stressPeakUsers(500).during(5) // 순간적으로 500명의 유저이 요청
			),
			rampLoadScenarioParallel.injectOpen(
				rampUsersPerSec(10).to(300).during(5).randomized() // 초당 10명의 유저에서 3000명의 유저가 5초동안 비균등하게 증가하는 방식으로 요청
			),
			burstLoadScenario.injectOpen(
				nothingFor(Duration.ofSeconds(5)), // 5초간 정지
				atOnceUsers(100), // 한번에 100명의 유저가 동시에 요청
				stressPeakUsers(500).during(5) // 순간적으로 500명의 유저이 요청
			),
			rampLoadScenario.injectOpen(
				rampUsersPerSec(10).to(300).during(5).randomized() // 초당 10명의 유저에서 3000명의 유저가 5초동안 비균등하게 증가하는 방식으로 요청
			)
		).protocols(httpProtocol);
	}
}
