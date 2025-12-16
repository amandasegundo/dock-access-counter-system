local value = redis.call('GET', KEYS[1])
local current = tonumber(value)

if current == nil then
    current = 0
end

local limit = tonumber(tostring(ARGV[1])) or 0

if current + 1 > limit then
    return -1
else
    return redis.call('INCR', KEYS[1])
end


