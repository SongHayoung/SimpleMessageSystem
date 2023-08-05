local val = redis.call('get', KEYS[1])
if not val then
    return false
end
redis.call('del', KEYS[1])
redis.call('hdel', KEYS[2], KEYS[1])
return true
