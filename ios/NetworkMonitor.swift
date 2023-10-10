import Foundation
import React
import Reachability

@objc(NetworkMonitor)
class NetworkMonitor: RCTEventEmitter {
  
  override init(){
      super.init()
  }
  
  let reachability = try! Reachability()
  
  @objc
  func isNetworkReachable(_ resolve: RCTPromiseResolveBlock?, reject: RCTPromiseRejectBlock?) {
    let isReachable = reachability.connection == .wifi || reachability.connection == .cellular

    resolve?(isReachable)
    return
  }
   
  @objc
  public func startMonitoring() {
    do{
      NotificationCenter.default.addObserver(self, selector: #selector(reachabilityChanged(note:)), name: .reachabilityChanged, object: reachability)
      try reachability.startNotifier()
    }catch{
      print("could not start reachability notifier")
    }
  }

  /**
      handle network change
   */
  @objc
  func reachabilityChanged(note: Notification) {

    guard let reachability = note.object as? Reachability
    else{
      return
    }

    switch reachability.connection {
    case .wifi , .cellular:
        sendEvent(withName: "onChange", body: ["status": "connected", "connectionType": reachability.connection] as [String : Any])
      break
    case .unavailable , .none:
      sendEvent(withName: "onChange", body: ["status": "disconnected", "connectionType": nil])
      break
    }

  }

  /**
      stop listening for network changes
   */
  @objc
  public func stopMonitoring() {
    reachability.stopNotifier()
    NotificationCenter.default.removeObserver(self, name: .reachabilityChanged, object: reachability)
  }

  /**
   we need to override this method and return an array of event names that we can listen to
   */
  open override func supportedEvents() -> [String]! {
    return ["onChange"]
  }

  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }
    
  @objc
  override func addListener(_ eventName: String!) {
    //Keep: Required for RN built in Event Emitter Calls.
  }

  @objc
  override func removeListeners(_ count: Double) {
    //Keep: Required for RN built in Event Emitter Calls.
  }
}
