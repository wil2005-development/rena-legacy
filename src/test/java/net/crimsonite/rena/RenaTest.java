/*
 * Copyright (C) 2020-2021  Nhalrath
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.crimsonite.rena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.database.DBConnection;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class RenaTest {
	
	@Test
	public void conn() {
		DBConnection.conn();
	}
	
	@Test
	public void start() throws JsonProcessingException, IOException, LoginException, IllegalArgumentException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode configRoot = mapper.readTree(new File("./config.json"));
		
		int totalShards = configRoot.get("SHARD_COUNT").asInt();
		boolean useSharding = configRoot.get("USE_SHARDING").asBoolean();			
        
		DefaultShardManagerBuilder jdaBuilder = DefaultShardManagerBuilder.createDefault(configRoot.get("TOKEN").asText())
			.setStatus(OnlineStatus.ONLINE)
			.enableIntents(GatewayIntent.GUILD_MEMBERS)
			.setMemberCachePolicy(MemberCachePolicy.ALL);
			
			if (useSharding) {				
				List<Integer> shardIds = new ArrayList<>();
				
				for (int i = 0; i < totalShards; i++) {
					shardIds.add(i);
				}
				
				jdaBuilder.setShardsTotal(totalShards)
							.setShards(shardIds);
			}
			
			jdaBuilder.build();
		}

}
