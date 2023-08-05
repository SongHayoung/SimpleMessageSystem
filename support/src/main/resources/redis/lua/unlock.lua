local val = redis.call('get', KEYS[1])
if not val then
    return false
end
if val == ARGV[1] then
    redis.call('unlink', KEYS[1])
    redis.call('hdel', KEYS[2], KEYS[1])
    return true
else
    return false
end