package flaxbeard.immersivepetroleum.common.data.builders;

import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

public class IPRecipeBuilder<B extends IPRecipeBuilder<B>>
{
    protected final List<ICondition> conditions = new ArrayList<>();

    public B addCondition(ICondition condition)
    {
        conditions.add(condition);
        return (B)this;
    }

    protected ICondition[] getConditions()
    {
        return conditions.toArray(ICondition[]::new);
    }
}
