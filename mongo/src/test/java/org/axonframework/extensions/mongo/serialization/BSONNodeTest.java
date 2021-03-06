/*
 * Copyright (c) 2010-2020. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.extensions.mongo.serialization;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class validating the {@link BSONNode}.
 *
 * @author Allard Buijze
 */
class BSONNodeTest {

    @Test
    void testNodeStructure() {
        BSONNode node = new BSONNode("root");
        BSONNode child = node.addChildNode("child");
        child.setValue("childValue");
        child.setAttribute("attr", "val");
        node.addChildNode("valueOnly").setValue("onlyValue");
        node.addChildNode("singleChildOwner").addChildNode("grandChild").setValue("value");

        BSONNode parsedNode = BSONNode.fromDBObject(node.asDBObject());

        assertEquals(node.asDBObject().toString(), parsedNode.asDBObject().toString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    void prefixesAttributesWithCorrectPrefix() {
        BSONNode node = new BSONNode("root");
        node.setAttribute("test", "attribute");

        assertEquals("attribute", ((Map) node.asDBObject().get("root")).get("attr_test"));
    }

    @Test
    void parsesPrefixedAttributesCorrectly() {
        DBObject attribute = new BasicDBObject();
        attribute.put("attr_test", "attribute");
        DBObject root = new BasicDBObject();
        root.put("root", attribute);

        BSONNode parsedNode = BSONNode.fromDBObject(root);

        assertEquals("attribute", parsedNode.getAttribute("test"));
    }

    @Test
    void setsValueFromChildElement() {
        DBObject value = new BasicDBObject();
        value.put("_value", "node value");

        BSONNode parsedNode = BSONNode.fromDBObject(value);

        assertEquals("node value", parsedNode.getValue());
    }
}
