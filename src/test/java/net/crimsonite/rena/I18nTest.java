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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.crimsonite.rena.core.I18n;

public class I18nTest {

    @Test
    public void getMessage() {
        String message = I18n.getMessage("misc.dice.unable_to_roll");

        assertEquals("*Sorry, I can't roll that for you*", message);
    }

}
