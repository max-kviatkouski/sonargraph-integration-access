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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockIssue;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public final class DuplicateCodeBlockIssueImpl extends MultiNamedElementIssueImpl implements IDuplicateCodeBlockIssue
{
    private static final long serialVersionUID = 3572308291532903170L;
    private int blockSize;
    private final List<IDuplicateCodeBlockOccurrence> occurrences;

    public DuplicateCodeBlockIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final IIssueProvider provider, final List<IDuplicateCodeBlockOccurrence> occurrences)
    {
        super(name, presentationName, description, issueType, provider);
        this.occurrences = occurrences;
    }

    @Override
    public List<INamedElement> getNamedElements()
    {
        return Collections.unmodifiableList(occurrences.stream().map(o -> o.getSourceFile()).distinct().collect(Collectors.toList()));
    }

    @Override
    public List<INamedElement> getAffectedNamedElements()
    {
        return Collections.unmodifiableList(occurrences.stream().map(o -> o.getSourceFile()).distinct().collect(Collectors.toList()));
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder("Duplicate Code Block with ");
        builder.append(occurrences.size()).append(" occurrences, block size '").append(blockSize).append("', resolved '").append(hasResolution())
                .append("'");
        occurrences.forEach(occ -> builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(Utility.INDENTATION)
                .append("Occurrence in ").append(occ.getSourceFile().getPresentationName()).append(", start '").append(occ.getStartLine())
                .append("', block size '").append(occ.getBlockSize()).append("', tolerance '").append(occ.getTolerance()).append("'"));
        return builder.toString();
    }

    public void setBlockSize(final int blockSize)
    {
        assert blockSize > 0 : "Parameter 'blockSize' of method 'setBlockSize' must be > 0";
        this.blockSize = blockSize;
    }

    @Override
    public int getBlockSize()
    {
        return blockSize;
    }

    @Override
    public List<IDuplicateCodeBlockOccurrence> getOccurrences()
    {
        return Collections.unmodifiableList(occurrences);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + blockSize;
        result = prime * result + ((occurrences == null) ? 0 : occurrences.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        final DuplicateCodeBlockIssueImpl other = (DuplicateCodeBlockIssueImpl) obj;
        if (blockSize != other.blockSize)
        {
            return false;
        }
        if (occurrences == null)
        {
            if (other.occurrences != null)
            {
                return false;
            }
        }
        else if (!occurrences.equals(other.occurrences))
        {
            return false;
        }
        return true;
    }
}