package com.gameloft.simpleCluster.Client

import akka.actor.{ActorPath, ActorSystem}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import com.typesafe.config.ConfigFactory

/**
  * Created by mbenabdelkerim on 11/10/15.
  */
object Main {
  def go: Unit ={
    val config = ConfigFactory.parseString(
      """
        |akka{
        |   actor {
        |     provider = "akka.remote.RemoteActorRefProvider"
        |   }
        |
        |   remote {
        |     transport ="akka.remote.netty.NettyRemoteTransport"
        |     log-remote-lifecycle-events = off
        |     netty.tcp {
        |       hostname = "127.0.0.1"
        |       port = 2550
        |     }
        |   }
        |}
      """.stripMargin)
    val system = ActorSystem("Client", ConfigFactory.load(config))
    val initialContacts = Set(
      ActorPath.fromString("akka.tcp://BrokersCluster@127.0.0.1:5000/system/receptionist"),
      ActorPath.fromString("akka.tcp://BrokersCluster@127.0.0.1:5001/system/receptionist")
    )
    val settings = ClusterClientSettings(system).withInitialContacts(initialContacts)

    val clientActor = system.actorOf(ClusterClient.props(settings), "client")
    for(i <- 1 to 10)
      clientActor ! ClusterClient.Send("/user/Broker", "Hello_"+i.toString, localAffinity = true)
  }
}
