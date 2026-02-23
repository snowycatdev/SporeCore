package me.clearedSpore.sporeCore.features.eco

import com.github.benmanes.caffeine.cache.Caffeine
import me.clearedSpore.sporeAPI.util.Cooldown
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object PaymentCooldownService {

    private const val SHORT_ID = "eco_pay_short"
    private const val LONG_ID = "eco_pay_spam"

    private val spamTracker = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build<UUID, AtomicInteger>()

    fun init() {
        Cooldown.createCooldown(SHORT_ID, 5)
        Cooldown.createCooldown(LONG_ID, 15 * 60)
    }

    fun canPay(uuid: UUID): Boolean {
        return !Cooldown.isOnCooldown(SHORT_ID, uuid) &&
                !Cooldown.isOnCooldown(LONG_ID, uuid)
    }

    fun onPayment(uuid: UUID) {
        Cooldown.addCooldown(SHORT_ID, uuid)

        val counter = spamTracker.get(uuid) { AtomicInteger(0) }
        if (counter.incrementAndGet() >= 10) {
            Cooldown.addCooldown(LONG_ID, uuid)
            spamTracker.invalidate(uuid)
        }
    }

    fun getRemaining(uuid: UUID): Long {
        return maxOf(
            Cooldown.getTimeLeft(SHORT_ID, uuid),
            Cooldown.getTimeLeft(LONG_ID, uuid)
        )
    }
}
