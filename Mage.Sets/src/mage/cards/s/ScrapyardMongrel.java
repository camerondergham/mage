
package mage.cards.s;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.common.PermanentsOnTheBattlefieldCondition;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.effects.common.continuous.GainAbilitySourceEffect;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.constants.Zone;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledArtifactPermanent;

/**
 *
 * @author Quercitron
 */
public final class ScrapyardMongrel extends CardImpl {

    public ScrapyardMongrel(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{3}{R}");
        this.subtype.add(SubType.HOUND);

        this.color.setRed(true);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // As long as you control an artifact, Scrapyard Mongrel gets +2/+0 and has trample.
        FilterPermanent filter = new FilterControlledArtifactPermanent();
        Effect boostEffect = new ConditionalContinuousEffect(
                new BoostSourceEffect(2, 0, Duration.WhileOnBattlefield),
                new PermanentsOnTheBattlefieldCondition(filter),
                "As long as you control an artifact, {this} gets +2/+0");
        Effect gainAbilityEffect = new ConditionalContinuousEffect(
                new GainAbilitySourceEffect(TrampleAbility.getInstance(), Duration.WhileOnBattlefield),
                new PermanentsOnTheBattlefieldCondition(filter),
                "and has trample");
        Ability ability = new SimpleStaticAbility(Zone.BATTLEFIELD, boostEffect);
        ability.addEffect(gainAbilityEffect);
        this.addAbility(ability);
    }

    public ScrapyardMongrel(final ScrapyardMongrel card) {
        super(card);
    }

    @Override
    public ScrapyardMongrel copy() {
        return new ScrapyardMongrel(this);
    }
}
