/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Connector extends ArchitectureElement
{
    private final Artifact m_parent;
    private final boolean m_isOptional;
    private final List<Connector> m_includedConnectors = new ArrayList<>();
    private final List<Interface> m_connectedInterfaces = new ArrayList<>();

    public Connector(Artifact parent, String name, boolean isOptional)
    {
        super(name);

        assert parent != null;
        m_parent = parent;
        m_isOptional = isOptional;
    }

    public boolean isOptional()
    {
        return m_isOptional;
    }

    public Artifact getParent()
    {
        return m_parent;
    }

    public void addIncludedConnector(Connector included)
    {
        assert included != null;

        m_includedConnectors.add(included);
    }

    public List<Connector> getIncludedConnectors()
    {
        return Collections.unmodifiableList(m_includedConnectors);
    }

    public void addConnectedInterface(Interface connection)
    {
        assert connection != null;

        m_connectedInterfaces.add(connection);
    }

    public List<Interface> getConnectedInterfaces()
    {
        return Collections.unmodifiableList(m_connectedInterfaces);
    }
}
