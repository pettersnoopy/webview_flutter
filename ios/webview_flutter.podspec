#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'webview_flutter'
  s.version          = '0.0.1'
  s.summary          = 'A WebView Plugin for Flutter.'
  s.description      = <<-DESC
A WebView Plugin for Flutter.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  
  s.dependency 'Flutter'
  s.dependency 'QQ_MTA'
  s.dependency 'QQ_MTA/Hybrid'
  
  s.ios.deployment_target = '8.0'
end

