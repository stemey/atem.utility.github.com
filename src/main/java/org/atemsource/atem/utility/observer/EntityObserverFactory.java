/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.observer;

import javax.inject.Inject;
import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilderFactory;
import org.atemsource.atem.utility.snapshot.SnapshotBuilder;
import org.atemsource.atem.utility.snapshot.SnapshotBuilderFactory;
import org.atemsource.atem.utility.transform.api.Transformation;


public class EntityObserverFactory
{

	@Inject
	private BeanLocator beanLocator;

	private ComparisonBuilderFactory comparisonBuilderFactory;

	private SnapshotBuilderFactory snapshotBuilderFactory;

	public EntityObserver create(Comparison comparison)
	{
		return createDefinition(comparison).create();

	}

	public EntityObserverDefinition createDefinition(Comparison comparison)
	{
		SnapshotBuilder snapshotBuilder = snapshotBuilderFactory.create(comparison.getEntityType());
		snapshotBuilder.include(comparison);
		Transformation<Object, Object> transformation = (Transformation<Object, Object>) snapshotBuilder.create();

		ComparisonBuilder comparisonBuilder = comparisonBuilderFactory.create((EntityType<?>) transformation.getTypeB());
		comparisonBuilder.include(comparison);
		Comparison snapshotComparison = comparisonBuilder.create();

		EntityObserverDefinition entityObserverDefinition = beanLocator.getInstance(EntityObserverDefinition.class);
		entityObserverDefinition.setComparison(snapshotComparison);
		entityObserverDefinition.setSnapshotting(transformation);
		return entityObserverDefinition;

	}

	public ObserverFactory createFactory(Comparison comparison)
	{
		SnapshotBuilder snapshotBuilder = snapshotBuilderFactory.create(comparison.getEntityType());
		snapshotBuilder.include(comparison);
		Transformation<Object, Object> transformation = (Transformation<Object, Object>) snapshotBuilder.create();

		ComparisonBuilder comparisonBuilder = comparisonBuilderFactory.create((EntityType<?>) transformation.getTypeB());
		comparisonBuilder.include(comparison);
		Comparison snapshotComparison = comparisonBuilder.create();
		ObserverFactory factory = new ObserverFactory(transformation, snapshotComparison);
		return factory;

	}

	public ComparisonBuilderFactory getComparisonBuilderFactory()
	{
		return comparisonBuilderFactory;
	}

	public SnapshotBuilderFactory getSnapshotBuilderFactory()
	{
		return snapshotBuilderFactory;
	}

	public void setComparisonBuilderFactory(ComparisonBuilderFactory comparisonBuilderFactory)
	{
		this.comparisonBuilderFactory = comparisonBuilderFactory;
	}

	public void setSnapshotBuilderFactory(SnapshotBuilderFactory snapshotBuilderFactory)
	{
		this.snapshotBuilderFactory = snapshotBuilderFactory;
	}
}
