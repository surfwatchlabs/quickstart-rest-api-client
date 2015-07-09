require 'rest_client'
require 'json'

url = 'https://www.surfwatchlabs.com:443/api/v3/feedRiskScores'

# Get feed risk scores from yesterday
header = {
  'content_type' => 'application/json',
  'app_key' => ENV['SURFWATCH_ANALYTICS_APP_KEY'],
  'app_id' => ENV['SURFWATCH_ANALYTICS_APP_ID'],
  :params => { 'yesterday' => 'true' }
}

puts
puts "Request : GET, url : #{url}"

response = RestClient.get(url, header)
puts
puts "Response from /yesterday resource : #{response}"

results = JSON.parse(response)
puts
puts "Found #{results.size} feed risk scores from yesterday."
puts

results.each do |irs|
  if irs['feed_risk'] > 50
    str_bldr = "Found a feed score over 50 : "
    str_bldr << "feed="
    str_bldr << irs['feed_description']
    str_bldr << ", risk_score="
    str_bldr << irs['feed_risk'].to_s
    str_bldr << ", analytic_day="
    str_bldr << irs['analytic_day'].to_s
    puts str_bldr
  end
end

puts
puts "Well that was fun!"
puts