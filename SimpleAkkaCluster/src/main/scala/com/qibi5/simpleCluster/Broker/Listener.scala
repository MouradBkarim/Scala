package com.gameloft.simpleCluster.Broker

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class Listener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart 
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberUp], classOf[UnreachableMember], classOf[MemberRemoved])
    //#subscribe
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) =>{
      log.info("Member is Up: {}", member.address)
    }
    case UnreachableMember(member) =>{
      log.info("Member detected as unreachable: {}", member)
      
    }
    case MemberRemoved(member, previousStatus) =>{
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
      
    }
//    case _: MemberEvent => // ignore

    // Receive msg from Producer
    case msg: String =>{
      println("I receive from Client : "+ msg)
    }
  }
}
