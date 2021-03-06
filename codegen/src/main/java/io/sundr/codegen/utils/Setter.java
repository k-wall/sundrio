/*
 *      Copyright 2018 The original authors.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package io.sundr.codegen.utils;

import io.sundr.codegen.DefinitionRepository;
import io.sundr.codegen.model.Method;
import io.sundr.codegen.model.Property;
import io.sundr.codegen.model.TypeDef;

import static io.sundr.codegen.utils.StringUtils.capitalizeFirst;

public class Setter {

    public static boolean has(TypeDef clazz, Property property) {
        for (Method method : clazz.getMethods()) {
            if (isApplicable(method, property)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isApplicable(Method method, Property property) {
        return isApplicable(method, property, false);
    }

    /**
    * Returns true if method is a getter of property.
    * In strict mode it will not strip non-alphanumeric characters.
    */
    private static boolean isApplicable(Method method, Property property, boolean strict) {
        if (method.getArguments().size() != 1) {
            return false;
        }

        if (!method.getArguments().get(0).getTypeRef().equals(property.getTypeRef())) {
            return false;
        }

        String capitalized = capitalizeFirst(property.getName());
        if (method.getName().endsWith("set" + capitalized)) {
            return true;
        }

        if (!strict && method.getName().endsWith("set" + property.getNameCapitalized())) {
            return true;
        }
        return false;
    }

    public static boolean hasOrInherits(TypeDef clazz, Property property) {
        TypeDef current = clazz;
        //Iterate parent objects and check for properties with setters but not ctor arguments.
        while (current!= null && !current.equals(TypeDef.OBJECT)) {
            for (Method method : current.getMethods()) {
                if (isApplicable(method, property)) {
                    return true;
                }
            }

            if (!current.getExtendsList().isEmpty()) {
                String fqn = current.getExtendsList().iterator().next().getDefinition().getFullyQualifiedName();
                current = DefinitionRepository.getRepository().getDefinition(fqn);
            } else {
                current = null;
            }
        }
        return false;
    }
}
