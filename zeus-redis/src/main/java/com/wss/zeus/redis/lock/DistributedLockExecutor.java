package com.wss.zeus.redis.lock;

import com.wss.zeus.core.exception.BizException;
import com.wss.zeus.core.exception.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁执行器
 * <p>
 * 封装 Redisson 分布式锁的获取、释放逻辑，业务层只需关注业务逻辑
 *
 * @author wangshusheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockExecutor {

    private final RedissonClient redissonClient;

    /**
     * 在分布式锁保护下执行业务逻辑（有返回值）
     *
     * @param lockKey       锁的 Key
     * @param waitTime      获取锁的等待时间（秒）
     * @param businessLogic 业务逻辑（Supplier）
     * @param <T>           返回值类型
     * @return 业务逻辑的返回值
     * @throws BizException 获取锁失败时抛出重复提交异常
     */
    public <T> T executeWithLock(String lockKey, long waitTime, Supplier<T> businessLogic) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean locked = lock.tryLock(waitTime, TimeUnit.SECONDS);
            if (!locked) {
                throw new BizException(SystemException.REPEAT);
            }

            return businessLogic.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException(SystemException.REPEAT);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 在分布式锁保护下执行业务逻辑（无返回值）
     *
     * @param lockKey       锁的 Key
     * @param waitTime      获取锁的等待时间（秒）
     * @param businessLogic 业务逻辑（Runnable）
     * @throws BizException 获取锁失败时抛出重复提交异常
     */
    public void executeWithLock(String lockKey, long waitTime, Runnable businessLogic) {
        executeWithLock(lockKey, waitTime, () -> {
            businessLogic.run();
            return null;
        });
    }

    /**
     * 在分布式锁保护下执行业务逻辑（使用默认等待时间 1 秒）
     *
     * @param lockKey       锁的 Key
     * @param businessLogic 业务逻辑（Supplier）
     * @param <T>           返回值类型
     * @return 业务逻辑的返回值
     * @throws BizException 获取锁失败时抛出重复提交异常
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> businessLogic) {
        return executeWithLock(lockKey, 1L, businessLogic);
    }

    /**
     * 在分布式锁保护下执行业务逻辑（使用默认等待时间 1 秒，无返回值）
     *
     * @param lockKey       锁的 Key
     * @param businessLogic 业务逻辑（Runnable）
     * @throws BizException 获取锁失败时抛出重复提交异常
     */
    public void executeWithLock(String lockKey, Runnable businessLogic) {
        executeWithLock(lockKey, 1L, businessLogic);
    }

    /**
     * 尝试在分布式锁保护下执行业务逻辑（获取锁失败时静默跳过，不抛异常）
     * <p>
     * 适用于：幂等场景，获取锁失败说明其他线程正在处理，直接跳过
     *
     * @param lockKey       锁的 Key
     * @param waitTime      获取锁的等待时间（秒）
     * @param businessLogic 业务逻辑（Runnable）
     * @return true-成功获取锁并执行，false-获取锁失败跳过执行
     */
    public boolean tryExecuteWithLock(String lockKey, long waitTime, Runnable businessLogic) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean locked = lock.tryLock(waitTime, TimeUnit.SECONDS);
            if (!locked) {
                return false;
            }

            businessLogic.run();
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 尝试在分布式锁保护下执行业务逻辑（使用默认等待时间 1 秒，获取锁失败静默跳过）
     *
     * @param lockKey       锁的 Key
     * @param businessLogic 业务逻辑（Runnable）
     * @return true-成功获取锁并执行，false-获取锁失败跳过执行
     */
    public boolean tryExecuteWithLock(String lockKey, Runnable businessLogic) {
        return tryExecuteWithLock(lockKey, 1L, businessLogic);
    }
}
