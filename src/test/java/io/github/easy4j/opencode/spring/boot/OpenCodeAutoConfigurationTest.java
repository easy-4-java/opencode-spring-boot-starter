/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.easy4j.opencode.spring.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {{ @link OpenCodeAutoConfiguration }}.
 *
 * <p>Verifies the auto-configuration activates under the expected conditions
 * and exposes its declared beans.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("OpenCodeAutoConfiguration Tests")
class OpenCodeAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    @DisplayName("Auto-configuration class can be instantiated")
    void testInstantiation() {
        OpenCodeAutoConfiguration configuration = new OpenCodeAutoConfiguration();
        assertThat(configuration).isNotNull();
    }

    @Test
    @DisplayName("Auto-configuration loads when 'opencode.enabled=true'")
    void testLoadsWhenEnabledPropertySet() {
        runner.withUserConfiguration(OpenCodeAutoConfiguration.class)
                .withPropertyValues("opencode.enabled=true", "opencode.debug.enabled=true",
                        "opencode.debug.level=HEADERS", "opencode.debug.max-content-length=4096")
                .run(context -> {
                    assertThat(context).hasSingleBean(OpenCodeAutoConfiguration.class);
                    OpenCodeProperties properties = context.getBean(OpenCodeProperties.class);
                    assertThat(properties.getDebug().isEnabled()).isTrue();
                    assertThat(properties.getDebug().getLevel().name()).isEqualTo("HEADERS");
                    assertThat(properties.getDebug().getMaxContentLength()).isEqualTo(4096);
                    assertThat(properties.getHttp().getDebug()).isSameAs(properties.getDebug());
                    assertThat(properties.getCli().getDebug()).isSameAs(properties.getDebug());
                });
    }

    @Test
    @DisplayName("Auto-configuration is absent when 'opencode.enabled=false'")
    void testNotLoadedWhenDisabled() {
        runner.withUserConfiguration(OpenCodeAutoConfiguration.class)
                .withPropertyValues("opencode.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(OpenCodeAutoConfiguration.class));
    }
}
