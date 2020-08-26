package org.light_novel.lkadmin.ui.article

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.light_novel.lkadmin.R
import org.light_novel.lkadmin.logic.model.Article
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArticleAdapter(val context: Context, private val articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val bannerImage: ImageView = view.findViewById(R.id.bannerImage)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val aidText: TextView = view.findViewById(R.id.aidText)
        val groupNameText: TextView = view.findViewById(R.id.groupNameText)
        val maskText: TextView = view.findViewById(R.id.maskText)
        val onlyPasser: TextView = view.findViewById(R.id.onlyPasser)
        val authorText: TextView = view.findViewById(R.id.authorText)
        val authorUidText: TextView = view.findViewById(R.id.authorUidText)
        val timeText: TextView = view.findViewById(R.id.timeText)
        val lastTimeText: TextView = view.findViewById(R.id.lastTimeText)
        val hideButton: Button = view.findViewById(R.id.hideButton)
        val topButton: Button = view.findViewById(R.id.topButton)
        val linkCommentButton: Button = view.findViewById(R.id.linkCommentButton)
        val scoreButton: Button = view.findViewById(R.id.scoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        val holder = ViewHolder(view)
        holder.authorText.setOnClickListener {
            val position = holder.adapterPosition
            val article = articles[position]
            (parent.context as ArticleActivity).findArticleAuthor(article.uid)
        }
        holder.hideButton.setOnClickListener {
            val position = holder.adapterPosition
            val article = articles[position]
            if (article.mask == 0) {
                (parent.context as ArticleActivity).articleMask(article.aid, 1)
            } else {
                (parent.context as ArticleActivity).articleMask(article.aid, 0)
            }
        }
        holder.topButton.setOnClickListener {
            val position = holder.adapterPosition
            val article = articles[position]
            val isTop = try {
                LocalDate.now().year + 100 == LocalDateTime.parse(article.lastTime, DateTimeFormatter.ISO_DATE_TIME).year
            } catch (e: Exception) {
                false
            }
            if (isTop) {
                (parent.context as ArticleActivity).articleTop(article.aid, false)
            } else {
                (parent.context as ArticleActivity).articleTop(article.aid, true)
            }
        }
        holder.linkCommentButton.setOnClickListener {
            val position = holder.adapterPosition
            val article = articles[position]
            (parent.context as ArticleActivity).linkComment(article.aid)
        }
        holder.scoreButton.setOnClickListener {
            val position = holder.adapterPosition
            val article = articles[position]
            (parent.context as ArticleActivity).findOneUser(article.uid)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        /*if (article.banner.isNotEmpty()) {
            Glide.with(context).load(article.banner).error(R.drawable.akkariin).into(holder.bannerImage)
        } else {
            Glide.with(context).load(R.drawable.akkariin).into(holder.bannerImage)
        }*/
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        holder.titleText.text = article.title
        val showAid = "#${article.aid}"
        holder.aidText.text = showAid
        val showGroupName = "[${article.group.groupName}]"
        holder.groupNameText.text = showGroupName
        holder.maskText.text = if (article.mask == 0) {
            holder.maskText.setTextColor(ContextCompat.getColor(context, R.color.statusNormal))
            holder.maskText.background = ContextCompat.getDrawable(context, R.drawable.status_normal)
            "正常"
        } else {
            holder.maskText.setTextColor(ContextCompat.getColor(context, R.color.statusHide))
            holder.maskText.background = ContextCompat.getDrawable(context, R.drawable.status_hide)
            "隐藏"
        }
        holder.onlyPasser.text = if (article.onlyPasser == 0) {
            holder.onlyPasser.setTextColor(ContextCompat.getColor(context, R.color.passer0))
            holder.onlyPasser.background = ContextCompat.getDrawable(context, R.drawable.text_passer0)
            "全部可见"
        } else {
            holder.onlyPasser.setTextColor(ContextCompat.getColor(context, R.color.passer1))
            holder.onlyPasser.background = ContextCompat.getDrawable(context, R.drawable.text_passer1)
            "勇者可见"
        }
        // holder.authorText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        holder.authorText.text = article.author.nickname
        val showUid = "[No.${article.uid}]"
        holder.authorUidText.text = showUid
        holder.timeText.text = LocalDateTime.parse(article.time, DateTimeFormatter.ISO_DATE_TIME).format(formatter)
        holder.lastTimeText.text = LocalDateTime.parse(article.lastTime, DateTimeFormatter.ISO_DATE_TIME).format(formatter)

        holder.hideButton.text = if (article.mask == 0) "隐藏" else "显示"
        val isTop = try {
            LocalDate.now().year + 100 == LocalDateTime.parse(article.lastTime, DateTimeFormatter.ISO_DATE_TIME).year
        } catch (e: Exception) {
            false
        }
        holder.topButton.text = if (isTop) "取消置顶" else "置顶"
    }

    override fun getItemCount() = articles.size
}