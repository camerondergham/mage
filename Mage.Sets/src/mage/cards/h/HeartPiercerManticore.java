
package mage.cards.h;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.InfoEffect;
import mage.abilities.effects.common.SendOptionUsedEventEffect;
import mage.abilities.keyword.EmbalmAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.target.common.TargetAnyTarget;

/**
 *
 * @author fireshoes
 */
public final class HeartPiercerManticore extends CardImpl {

    public HeartPiercerManticore(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{R}{R}");

        this.subtype.add(SubType.MANTICORE);
        this.power = new MageInt(4);
        this.toughness = new MageInt(3);

        // When Heart-Piercer Manticore enters the battlefield, you may sacrifice another creature.
        Ability firstAbility = new EntersBattlefieldTriggeredAbility(new HeartPiercerManticoreSacrificeEffect(), true);
        this.addAbility(firstAbility);
        // When you do, Heart-Piercer Manticore deals damage equal to that creature's power to any target.
        Ability secondAbility = new HeartPiercerManticoreSacrificeTriggeredAbility(firstAbility.getOriginalId());
        secondAbility.addTarget(new TargetAnyTarget());
        this.addAbility(secondAbility);
        // Embalm {5}{R}
        this.addAbility(new EmbalmAbility(new ManaCostsImpl("{5}{R}"), this));

    }

    public HeartPiercerManticore(final HeartPiercerManticore card) {
        super(card);
    }

    @Override
    public HeartPiercerManticore copy() {
        return new HeartPiercerManticore(this);
    }
}

class HeartPiercerManticoreSacrificeEffect extends OneShotEffect {

    public HeartPiercerManticoreSacrificeEffect() {
        super(Outcome.Damage);
        this.staticText = "you may sacrifice another creature";
    }

    public HeartPiercerManticoreSacrificeEffect(final HeartPiercerManticoreSacrificeEffect effect) {
        super(effect);
    }

    @Override
    public HeartPiercerManticoreSacrificeEffect copy() {
        return new HeartPiercerManticoreSacrificeEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Target target = new TargetControlledCreaturePermanent(1, 1, StaticFilters.FILTER_CONTROLLED_ANOTHER_CREATURE, true);
            if (controller.choose(outcome, target, source.getSourceId(), game)) {
                Permanent toSacrifice = game.getPermanent(target.getFirstTarget());
                if (toSacrifice != null) {
                    toSacrifice.sacrifice(source.getSourceId(), game);
                    return new SendOptionUsedEventEffect(toSacrifice.getPower().getValue()).apply(game, source);
                }
            }
            return true;
        }
        return false;
    }
}

class HeartPiercerManticoreSacrificeTriggeredAbility extends TriggeredAbilityImpl {

    private final UUID relatedTriggerdAbilityOriginalId;

    public HeartPiercerManticoreSacrificeTriggeredAbility(UUID relatedTriggerdAbilityOriginalId) {
        super(Zone.BATTLEFIELD, new InfoEffect("{this} deals damage equal to that creature's power to any target"));
        this.relatedTriggerdAbilityOriginalId = relatedTriggerdAbilityOriginalId;
    }

    public HeartPiercerManticoreSacrificeTriggeredAbility(final HeartPiercerManticoreSacrificeTriggeredAbility ability) {
        super(ability);
        this.relatedTriggerdAbilityOriginalId = ability.relatedTriggerdAbilityOriginalId;
    }

    @Override
    public HeartPiercerManticoreSacrificeTriggeredAbility copy() {
        return new HeartPiercerManticoreSacrificeTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.OPTION_USED;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getPlayerId().equals(this.getControllerId())
                && event.getTargetId().equals(relatedTriggerdAbilityOriginalId)
                && event.getSourceId().equals(getSourceId())) {
            getEffects().clear();
            getEffects().add(new DamageTargetEffect(event.getAmount()));
            return true;
        }
        return false;
    }

    @Override
    public String getRule() {
        return "When you do, {this} deals damage equal to that creature's power to any target.";
    }
}
