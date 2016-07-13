/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroup;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public final class CycleGroupImpl extends AbstractElementIssueImpl implements ICycleGroup
{
    private final IAnalyzer analyzer;
    private final List<INamedElement> cyclicElements = new ArrayList<>();

    public CycleGroupImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final boolean hasResolution, final IAnalyzer analyzer)
    {
        super(name, presentationName, description, issueType, provider, hasResolution, -1);
        assert analyzer != null : "Parameter 'analyzer' of method 'CycleGroup' must not be null";

        this.analyzer = analyzer;
    }

    public void setAffectedElements(final List<INamedElement> elements)
    {
        assert elements != null : "Parameter 'elements' of method 'setCyclicElements' must not be null";
        assert !elements.isEmpty() : "Parameter 'elements' of method 'setCyclicElements' must not be empty";
        cyclicElements.addAll(elements);
    }

    @Override
    public List<INamedElement> getAffectedElements()
    {
        return Collections.unmodifiableList(cyclicElements);
    }

    @Override
    public List<INamedElement> getOrigins()
    {
        return getAffectedElements();
    }

    @Override
    public IAnalyzer getAnalyzer()
    {
        return analyzer;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((analyzer == null) ? 0 : analyzer.hashCode());
        result = prime * result + ((cyclicElements == null) ? 0 : cyclicElements.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CycleGroupImpl other = (CycleGroupImpl) obj;
        if (analyzer == null)
        {
            if (other.analyzer != null)
            {
                return false;
            }
        }
        else if (!analyzer.equals(other.analyzer))
        {
            return false;
        }
        if (cyclicElements == null)
        {
            if (other.cyclicElements != null)
            {
                return false;
            }
        }
        else if (!cyclicElements.equals(other.cyclicElements))
        {
            return false;
        }
        return true;
    }
}