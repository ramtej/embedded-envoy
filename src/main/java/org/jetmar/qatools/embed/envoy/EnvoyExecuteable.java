/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,
	Archimedes Trajano (trajano@github),
	Kevin D. Keck (kdkeck@github),
	Ben McCann (benmccann@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetmar.qatools.embed.envoy;



import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;
import org.jetmar.qatools.embed.envoy.xxxx.GenericProcess;
import org.jetmar.qatools.embed.envoy.xxxx.GenericProcessConfig;

import java.io.IOException;

public class EnvoyExecuteable extends Executable<GenericProcessConfig, GenericProcess> {

	public EnvoyExecuteable(Distribution distribution, GenericProcessConfig config, IRuntimeConfig runtimeConfig,
                            IExtractedFileSet executable) {
		super(distribution, config, runtimeConfig, executable);
	}

	@Override
	protected GenericProcess start(Distribution distribution, GenericProcessConfig config, IRuntimeConfig runtime)
			throws IOException {
		// JJ TODO return new GenericProcess(distribution,config,runtime,this);
		return null;
	}
	
}