local val = redis.call('get', KEYS[1])
if not val then
    redis.call('set', KEYS[1], ARGV[1])
    redis.call('hset', KEYS[2], KEYS[1], ARGV[3])
    redis.call('expire', KEYS[1], ARGV[2])
    return true
end
return false