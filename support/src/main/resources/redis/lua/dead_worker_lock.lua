local val = redis.call('get', KEYS[1])
if not val or val < ARGV[1] then
    redis.call('set', KEYS[1], ARGV[1])
    redis.call('expire', KEYS[1], ARGV[2])
    return true
end
return false