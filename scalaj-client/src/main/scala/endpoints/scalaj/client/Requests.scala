package endpoints.scalaj.client

import endpoints.Tupler
import endpoints.algebra

import scalaj.http.HttpRequest

trait Requests extends algebra.Requests with Urls with Methods{


  override type RequestHeaders[A] = A => Seq[(String, String)]

  override type Request[A] = A => HttpRequest

  override type RequestEntity[A] = ((A, HttpRequest)) => HttpRequest

  override def emptyHeaders: RequestHeaders[Unit] = _ => Seq()

  override def emptyRequest: RequestEntity[Unit] = (x: (Unit, HttpRequest)) => x._2


  def request[U, E, H, UE](method: Method,
                           url: Url[U],
                           entity: RequestEntity[E] = emptyRequest,
                           headers: RequestHeaders[H] = emptyHeaders
                          )(implicit tuplerUE: Tupler.Aux[U, E, UE], tuplerUEH: Tupler[UE, H]): Request[tuplerUEH.Out] =
    (abc) => {
      val (ue, h) = tuplerUEH.unapply(abc)
      val (u, e) = tuplerUE.unapply(ue)
      val req = url.toReq(u)
        .headers(headers(h))
      entity((e, req))
        .method(method)
    }

}
