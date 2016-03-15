package com.gameloft.simpleCluster

import akka.cluster.client.ClusterClientReceptionist
import com.gameloft.simpleCluster.Broker.Listener
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props

object Main {
  def main(args: Array[String]): Unit = {
    if(args(0) == "c" || args(0) == "C"){
      // Start producer
      Client.Main.go
    }
    else if(args(0) == "N" || args(0) == "n"){
      startup(args(1))
    }
    else{
      println("Parameters missing !!!!")
    }
  }

  def startup(port: String): Unit = {
      // Override the configuration of the port
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load())

      // Create an Akka system
      val system = ActorSystem("BrokersCluster", config)
      // Create an actor that handles cluster domain events
      val service = system.actorOf(Props[Listener], name = "Broker")

      // Producer listener
     ClusterClientReceptionist(system).registerService(service)
  }

}

