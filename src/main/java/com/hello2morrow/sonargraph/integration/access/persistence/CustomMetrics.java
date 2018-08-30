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
package com.hello2morrow.sonargraph.integration.access.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;

public final class CustomMetrics
{
    private static final char CUSTOM_METRIC_SEPARATOR = '|';
    private static final String CUSTOM_METRIC_INT = "INT";
    private static final String CUSTOM_METRIC_FLOAT = "FLOAT";

    public interface ICustomMetricsProvider
    {
        /**
         * @return a non-null, non-empty name
         */
        public abstract String getHiddenDirectoryName();

        public default String getDirectory()
        {
            return System.getProperty("user.home") + "/." + getHiddenDirectoryName();
        }

        public default String getFileName()
        {
            return "metrics.properties";
        }

        public default String getFilePath()
        {
            return getDirectory() + "/" + getFileName();
        }

        public void error(String error, IOException exception);

        public void warning(String warning);

        public void info(String info);

        public void customMetricsSaved(String info);
    }

    private CustomMetrics()
    {
        super();
    }

    public static Properties loadCustomMetrics(final ICustomMetricsProvider customMetricsProvider)
    {
        assert customMetricsProvider != null : "Parameter 'customMetricsProvider' of method 'loadCustomMetrics' must not be null";

        final Properties customMetrics = new Properties();

        try (FileInputStream fis = new FileInputStream(new File(customMetricsProvider.getFilePath())))
        {
            customMetrics.load(fis);
            customMetricsProvider.info("Loaded custom metrics file '" + customMetricsProvider.getFilePath() + "'");
        }
        catch (final FileNotFoundException e)
        {
            customMetricsProvider.info("Custom metrics file '" + customMetricsProvider.getFilePath() + "' not found");
        }
        catch (final IOException e)
        {
            customMetricsProvider.error("Unable to load custom metrics file '" + customMetricsProvider.getFilePath() + "'", e);
        }

        return customMetrics;
    }

    public static String createCustomMetricKeyFromStandardName(final String keyPrefix, final String softwareSystemName, final String metricIdName)
    {
        assert keyPrefix != null : "Parameter 'keyPrefix' of method 'createCustomMetricKeyFromStandardName' must not be null";
        assert softwareSystemName != null && softwareSystemName
                .length() > 0 : "Parameter 'softwareSystemName' of method 'createCustomMetricKeyFromStandardName' must not be empty";
        assert metricIdName != null
                && metricIdName.length() > 0 : "Parameter 'metricIdName' of method 'createCustomMetricKeyFromStandardName' must not be empty";
        String customMetricKey = keyPrefix + softwareSystemName + "." + Utility.convertMixedCaseStringToConstantName(metricIdName).replace(" ", "");
        customMetricKey = customMetricKey.replace(CUSTOM_METRIC_SEPARATOR, ' ');
        return customMetricKey;
    }

    public static void addCustomMetric(final ISoftwareSystem softwareSystem, final IMetricId metricId, final Properties customMetrics,
            final int maxLengthDescription)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'addCustomMetric' must not be null";
        assert metricId != null : "Parameter 'metricId' of method 'addCustomMetric' must not be null";
        assert customMetrics != null : "Parameter 'customMetrics' of method 'addCustomMetric' must not be null";
        assert maxLengthDescription >= 0 : "'maxLengthDescription' must not be negative";

        customMetrics.put(softwareSystem.getName() + CUSTOM_METRIC_SEPARATOR + metricId.getName(),
                metricId.getPresentationName() + CUSTOM_METRIC_SEPARATOR + (metricId.isFloat() ? CUSTOM_METRIC_FLOAT : CUSTOM_METRIC_INT)
                        + CUSTOM_METRIC_SEPARATOR + metricId.getBestValue() + CUSTOM_METRIC_SEPARATOR + metricId.getWorstValue()
                        + CUSTOM_METRIC_SEPARATOR + Utility.trimDescription(metricId.getDescription(), maxLengthDescription));
    }

    public static void save(final ICustomMetricsProvider customMetricsProvider, final Properties customMetrics)
    {
        assert customMetricsProvider != null : "Parameter 'customMetricsProvider' of method 'save' must not be null";
        assert customMetrics != null : "Parameter 'customMetrics' of method 'save' must not be null";

        try
        {
            final File file = new File(customMetricsProvider.getDirectory());
            file.mkdirs();
            customMetrics.store(new FileWriter(new File(file, customMetricsProvider.getFileName())), "Custom Metrics");
            customMetricsProvider.customMetricsSaved("Custom metrics file '" + customMetricsProvider.getFilePath() + "' saved");
        }
        catch (final IOException e)
        {
            customMetricsProvider.error("Unable to save custom metrics file '" + customMetricsProvider.getFilePath() + "'", e);
        }
    }

    private static String getNonEmptyString(final Object input)
    {
        if (input instanceof String && !((String) input).isEmpty())
        {
            return (String) input;
        }
        throw new IllegalArgumentException("Empty input");
    }

    public interface ICustomMetricsConsumer
    {
        public void parsedFloatMetric(String nextMetricKey, String nextMetricPresentationName, String trimDescription, Double nextBestValue,
                Double nextWorstValue);

        public void parsedIntMetric(String nextMetricKey, String nextMetricPresentationName, String trimDescription, Double nextBestValue,
                Double nextWorstValue);

        public void unableToParseMetric(String string);
    }

    public static void parse(final Properties customMetrics, final String metricKeyPrefix, final int maxLengthDescription,
            final ICustomMetricsConsumer consumer)
    {
        assert customMetrics != null : "Parameter 'customMetrics' of method 'parse' must not be null";
        assert metricKeyPrefix != null : "Parameter 'metricKeyPrefix' of method 'parse' must not be null";
        assert maxLengthDescription >= 0 : "'maxLengthDescription' must not be negative";
        assert consumer != null : "Parameter 'consumer' of method 'parse' must not be null";

        for (final Entry<Object, Object> nextEntry : customMetrics.entrySet())
        {
            final String nextKey = getNonEmptyString(nextEntry.getKey());
            final String nextValue = getNonEmptyString(nextEntry.getValue());

            try
            {
                final String[] nextSplitKey = nextKey.split("\\" + CUSTOM_METRIC_SEPARATOR);
                final String[] nextSplitValue = nextValue.split("\\" + CUSTOM_METRIC_SEPARATOR);

                if (nextSplitKey.length == 2 && nextSplitValue.length == 5)
                {
                    final String nextSoftwareSystemName = nextSplitKey[0];
                    final String nextMetricIdName = nextSplitKey[1];

                    final String nextMetricKey = createCustomMetricKeyFromStandardName(metricKeyPrefix, nextSoftwareSystemName, nextMetricIdName);
                    final String nextMetricPresentationName = nextSplitValue[0];
                    final String nextTypeInfo = nextSplitValue[1];
                    final Double nextBestValue = Double.valueOf(nextSplitValue[2]);
                    final Double nextWorstValue = Double.valueOf(nextSplitValue[3]);
                    final String nextDescription = nextSplitValue[4];

                    if (CUSTOM_METRIC_FLOAT.equalsIgnoreCase(nextTypeInfo))
                    {
                        consumer.parsedFloatMetric(nextMetricKey, nextMetricPresentationName,
                                Utility.trimDescription(nextDescription, maxLengthDescription), nextBestValue, nextWorstValue);
                    }
                    else
                    {
                        consumer.parsedIntMetric(nextMetricKey, nextMetricPresentationName,
                                Utility.trimDescription(nextDescription, maxLengthDescription), nextBestValue, nextWorstValue);
                    }
                }
                else
                {
                    consumer.unableToParseMetric("Unable to parse custom metric from '" + nextKey + "=" + nextValue);
                }
            }
            catch (final Exception e)
            {
                consumer.unableToParseMetric("Unable to parse custom metric from '" + nextKey + "=" + nextValue + " - " + e.getLocalizedMessage());
            }
        }
    }
}